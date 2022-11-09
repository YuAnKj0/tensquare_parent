package com.tensquare.article.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tensquare.article.pojo.Article;
import com.tensquare.article.service.ArticleService;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 控制器层
 * @author Administrator
 *
 */
@RestController
@CrossOrigin
@RequestMapping("/article")
public class ArticleController {

	@Autowired
	private ArticleService articleService;
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	@PostMapping("/subscribe")
	private Result subscribe(@RequestBody Map map){
		Boolean flag =articleService.subscribe(map.get("userId").toString(),
				map.get("articleId").toString());
		if (flag) {
			return new Result(true, StatusCode.OK, "订阅成功");
		} else {
			return new Result(true, StatusCode.OK, "订阅取消");
		}
	}
	
	
	/**
	 * 查询全部数据
	 * @return
	 */
	@GetMapping
	public Result findAll(){
		return new Result(true, StatusCode.OK,"查询成功",articleService.findAll());
	}
	
	/**
	 * 根据ID查询
	 * @param id ID
	 * @return
	 */
	@GetMapping("/{id}")
	public Result findById(@PathVariable String id){
		return new Result(true,StatusCode.OK,"查询成功",articleService.findById(id));
	}


	/**
	 * 分页+多条件查询
	 * @param searchMap 查询条件封装
	 * @param page 页码
	 * @param size 页大小
	 * @return 分页结果
	 */
	@PostMapping("/search/{page}/{size}")
	public Result findSearch(@RequestBody Map searchMap , @PathVariable int page, @PathVariable int size){
		IPage<Article> pageList = articleService.findSearch(searchMap, page, size);
		return new Result(true,StatusCode.OK,"查询成功",  new PageResult<Article>((int) pageList.getTotal(), pageList.getRecords()) );
	}

	/**
     * 根据条件查询
     * @param searchMap
     * @return
     */
    @PostMapping(value="/search")
    public Result findSearch( @RequestBody Map searchMap){
        return new Result(true,StatusCode.OK,"查询成功",articleService.findSearch(searchMap));
    }
	
	/**
	 * 增加
	 * @param article
	 */
	@PostMapping
	public Result save(@RequestBody Article article  ){
		articleService.save(article);
		return new Result(true,StatusCode.OK,"增加成功");
	}
	
	/**
	 * 修改
	 * @param article
	 */
	@PutMapping("/{id}")
	public Result update(@RequestBody Article article, @PathVariable String id ){
		article.setId(id);
		articleService.update(article);		
		return new Result(true,StatusCode.OK,"修改成功");
	}
	
	/**
	 * 删除
	 * @param id
	 */
	@DeleteMapping(value="/{id}")
	public Result delete(@PathVariable String id ){
		articleService.deleteById(id);
		return new Result(true,StatusCode.OK,"删除成功");
	}

	/**
	 * 文章审核
	 * @param id
	 * @return
	 */
	@PutMapping(value="/examine/{id}")
	public Result examine(@PathVariable  String id){
		articleService.examine(id);
		return new Result(true,StatusCode.OK,"审核成功");
	}

	/**
	 * 文章点赞
	 * @param id
	 * @return
	 */
	@PutMapping(value="/thumbup/{id}")
	public Result updateThumpup(@PathVariable  String id){
		articleService.updateThumpup(id);
		return new Result(true,StatusCode.OK,"点赞成功");
		
	}
	@PutMapping("/thumbup/{articleId}")
	public Result thumbup(@PathVariable String articleId){
		//模拟用户id
		String userId = "4";
		String key = "thumbup_article_" + userId + "_" + articleId;
		//根据用户id和文章id,查询用户点赞信息，
		Object flag = redisTemplate.opsForValue().get(key);
		
		if (flag==null) {
			//可以点赞
			articleService.thumbup(articleId,userId);
			//点赞成功，保存点赞信息
			redisTemplate.opsForValue().set(key,1);
			return new Result(true, StatusCode.OK, "点赞成功");
		}
		//如果不为空，表示用户点过赞，不可以重复点赞
		return new Result(false, StatusCode.REPERROR, "不能重复点赞");
		
	}
	

	@GetMapping(value="/exception")
	public Result exception() throws Exception {
		throw new Exception("测试统一异常处理");
	}
	
}
