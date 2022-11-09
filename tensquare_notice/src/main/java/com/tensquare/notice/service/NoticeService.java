package com.tensquare.notice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tensquare.notice.client.ArticleClient;
import com.tensquare.notice.client.UserClient;
import com.tensquare.notice.dao.NoticeDao;
import com.tensquare.notice.dao.NoticeFreshDao;
import com.tensquare.notice.pojo.Notice;
import com.tensquare.notice.pojo.NoticeFresh;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import util.IdWorker;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ykj
 * @date 2022/11/7/14:11
 * @apiNote
 */

@Service
public class NoticeService {

    @Autowired
    private NoticeDao noticeDao;

    @Autowired
    private NoticeFreshDao noticeFreshDao;

    @Autowired
    private IdWorker idWorker;
    
    @Autowired
    private UserClient userClient;
    @Autowired
    private ArticleClient articleClient;
    
    
    
    
    public void getNoticeInfo(Notice notice){
        Result userResult = userClient.findById(notice.getOperatorId());
        HashMap userMap= (HashMap) userResult.getData();
        notice.setOperatorName(userMap.get("nickname").toString());
        
        //获取文章信息
        if ("article".equals(notice.getTargetType())) {
            Result articleResult =articleClient.findById(notice.getTargetId());
            HashMap articleMap= (HashMap) articleResult.getData();
            
            notice.setTargetName(articleMap.get("title").toString());
        }
    
    }    
    
    
    public Notice selectById(String id) {
        Notice notice = noticeDao.selectById(id);
        getNoticeInfo(notice);
        return notice;
    }

    public Page<Notice> selectByPage(Notice notice, Integer page, Integer size) {
        //封装分页对象
        Page<Notice> pageData = new Page<>(page, size);

        //执行分页查询
        //List<Notice> noticeList = noticeDao.selectPage(pageData, new EntityWrapper<>(notice));
        //for (Notice n : noticeList) {
        //    getNoticeInfo(n);
        //}

        //设置结果集到分页对象中
        //pageData.setRecords(noticeList);

        //返回
        return pageData;
    }

    public void save(Notice notice) {
        //设置初始值
        //设置状态 0表示未读  1表示已读
        notice.setState("0");
        notice.setCreatetime(new Date());

        //使用分布式Id生成器，生成id
        String id = idWorker.nextId() + "";
        notice.setId(id);
        noticeDao.insert(notice);

        //待推送消息入库，新消息提醒
        //NoticeFresh noticeFresh = new NoticeFresh();
        //noticeFresh.setNoticeId(id);//消息id
        //noticeFresh.setUserId(notice.getReceiverId());//待通知用户的id
        //noticeFreshDao.insert(noticeFresh);
    }

    public void updateById(Notice notice) {
        noticeDao.updateById(notice);
    }

    public Page<NoticeFresh> freshPage(String userId, Integer page, Integer size) {
        //封装查询条件
        NoticeFresh noticeFresh = new NoticeFresh();
        noticeFresh.setUserId(userId);

        //创建分页对象
        Page<NoticeFresh> pageData = new Page<>(page, size);

        //执行查询
        //List<NoticeFresh> list = noticeFreshDao.selectPage(pageData, new EntityWrapper<>(noticeFresh));

        //设置查询结果集到分页对象中
        //pageData.setRecords(list);

        //返回结果
        return pageData;
    }

    public void freshDelete(NoticeFresh noticeFresh) {
        //noticeFreshDao.delete(new EntityWrapper<>(noticeFresh));
    }
}
