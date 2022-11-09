package com.tensquare.article.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.protocol.x.Notice;
import com.tensquare.article.client.NoticeClient;
import com.tensquare.article.dao.ArticleDao;
import com.tensquare.article.pojo.Article;
import com.tensquare.article.vo.NoticeVo;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import util.IdWorker;
import util.MybatisPlusPubFuns;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 服务层
 * 
 * @author Administrator
 *
 */
@Service
public class ArticleService {

	@Autowired
	private ArticleDao articleDao;
	
	@Autowired
	private IdWorker idWorker;
	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private NoticeClient noticeClient;
	
	@Autowired
	private RabbitTemplate rabbitTemplate;

	/**
	 * 查询全部列表
	 * @return
	 */
	public List<Article> findAll() {
		return articleDao.selectList(null);
	}

	
	/**
	 * 条件查询+分页
	 * @param whereMap
	 * @param page
	 * @param size
	 * @return
	 */
	public IPage<Article> findSearch(Map whereMap, int page, int size) {
		
		QueryWrapper<Article> wapper = MybatisPlusPubFuns.createEntityWrapper(whereMap);
		// 执行查询
		IPage<Article> p = new Page<Article>(page, size);
		p = articleDao.selectPage(p, wapper);
		return p;
	}

	
	/**
	 * 条件查询
	 * @param whereMap
	 * @return
	 */
	public List<Article> findSearch(Map whereMap) {
		QueryWrapper<Article> wapper = MybatisPlusPubFuns.createEntityWrapper(whereMap);
		return articleDao.selectList(wapper);
	}


	

	/**
	 * 根据ID查询实体
	 * @param id
	 * @return
	 */
	public Article findById(String id) {
		Article article= (Article)redisTemplate.opsForValue().get("article_"+id);
		if(article==null){
			article=articleDao.selectById(id);//从数据库中查询
			redisTemplate.opsForValue().set("article_"+id,article,10, TimeUnit.SECONDS);//放入缓存

			System.out.println("从数据库中查询并放入缓存");
		}else{
			System.out.println("从缓存中提取数据");
		}
		return article;
	}

	/**
	 * 增加
	 * @param article
	 */
	public void save(Article article) {
		String id = idWorker.nextId() + "";
		article.setId(id);
		
		//初始化数据
		article.setVisits(0);   //浏览量
		article.setThumbup(0);  //点赞数
		article.setComment(0);  //评论数		
		articleDao.insert(article);
		
		//TODO 使用jwt获取当前用户的userid，也就是文章作者的id
		String authorId="3";
		//获取需要通知的读者
		String authorKey = "article_author_" + authorId;
		Set<String> set  = redisTemplate.boundSetOps(authorKey).members();
		for (String uid : set) {
			NoticeVo noticeVo=new NoticeVo();
			noticeVo.setReceiverId(uid);
			noticeVo.setOperatorId(authorId);
			noticeVo.setAction("publish");
			noticeVo.setTargetType("article");
			noticeVo.setTargetId(id);
			noticeVo.setCreatetime(new Date());
			noticeVo.setType("sys");
			noticeVo.setState("0");

			noticeClient.add(noticeVo);
			
			
			
		}
		
		rabbitTemplate.convertAndSend("article_subscribe",authorId,id);
		
	}

	/**
	 * 修改
	 * @param article
	 */
	public void update(Article article) {
		redisTemplate.delete("article_"+article.getId());
		articleDao.updateById(article);
	}

	/**
	 * 删除
	 * @param id
	 */
	public void deleteById(String id) {
		redisTemplate.delete("article_"+id);
		articleDao.deleteById(id);
	}

	/**
	 * 文章审核
	 * @param id
	 */
	@Transactional
	public void examine(String id){
		articleDao.examine(id);
	}

	/**
	 * 点赞
	 * @param id
	 */
	@Transactional
	public void updateThumpup(String id){
		articleDao.updateThumbup(id);
	}
	
	/**
	 * 取消或者订阅文章作者
	 * @param userId
	 * @param articleId
	 * @return
	 */
	public Boolean subscribe(String userId, String articleId) {
		//根据文章id查询文章作者id
	    String authorId = articleDao.selectById(articleId).getUserid();
		//创建Rabbit管理器
		RabbitAdmin rabbitAdmin = new RabbitAdmin(rabbitTemplate.getConnectionFactory());
		DirectExchange exchange = new DirectExchange("article_subscribe");
		rabbitAdmin.declareExchange(exchange);
		
		//创建队列
		Queue queue=new Queue("article_subscribe_" + userId, true);
		//声明exchange和queue的绑定关系，设置路由键为作者id
		Binding binding = BindingBuilder.bind(queue).to(exchange).with(authorId);
		
		//存放用户订阅作者
		String userKey = "article_subscribe_" + userId;
		//存放作者的订阅者
	    String authorKey = "article_author_" + authorId;
		
		//查询用户是否已经订阅作者
	    Boolean flag = redisTemplate.boundSetOps(userKey).isMember(authorId);
	    if (flag) {
		    //flag为TRUE，取消订阅
		    redisTemplate.boundSetOps(userKey).rename(authorId);
			redisTemplate.boundSetOps(authorKey).remove(userId);
			rabbitAdmin.removeBinding(binding);
			return false;
	    }else {
			redisTemplate.boundSetOps(userKey).add(authorId);
			redisTemplate.boundSetOps(authorKey).add(userId);
		    //声明队列和绑定队列
		    rabbitAdmin.declareQueue(queue);
		    rabbitAdmin.declareBinding(binding);
			
			return true;
	    }
	
	
    }
	
	/**
	 * 修改ArticleService的点赞方法，增加消息通知：
	 * @param articleId
	 */
	public void thumbup(String articleId,String userId) {
		Article article = articleDao.selectById(articleId);
		article.setThumbup(article.getThumbup()+1);
		articleDao.updateById(article);
		
		//点赞消息通知
		//TODO 实际是消息队列
		NoticeVo notice = new NoticeVo();
		notice.setReceiverId(article.getUserid());
		notice.setOperatorId(userId);
		notice.setAction("thumbup");
		notice.setTargetType("article");
		notice.setTargetId(articleId);
		notice.setCreatetime(new Date());
		notice.setType("user");
		notice.setState("0");

		noticeClient.add(notice);
		
		
	}
}
