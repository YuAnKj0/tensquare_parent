package com.tensquare.notice.config.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tensquare.notice.pojo.Notice;
import com.tensquare.notice.pojo.NoticeFresh;
import com.tensquare.notice.service.NoticeService;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Ykj
 * @date 2022/11/7/14:08
 * @apiNote
 */

@RestController
@CrossOrigin
@RequestMapping("notice")
public class NoticeController {
    
    @Autowired
    private NoticeService noticeService;
    
    
    // 1. 根据id查询消息通知
    // http://127.0.0.1:9014/notice/{id}  GET
    @GetMapping("/{id}")
    public Result selectById(@PathVariable String id){
        Notice notice=noticeService.selectById(id);
        return new Result(true, StatusCode.OK,"查询成功",notice);
    }    
    
    @PostMapping("/search/{page}/{size}")
    public Result selectByList(@RequestBody Notice notice,
                               @PathVariable Integer  page,
                               @PathVariable Integer  size){
        Page<Notice> pageData=noticeService.selectByPage(notice,page,size);
        PageResult<Notice> pageResult = new PageResult<>((int) pageData.getTotal(), pageData.getRecords());
        return new Result(true, StatusCode.OK, "查询成功", pageResult);
    }
    
    // 3. 新增通知
    // http://127.0.0.1:9014/notice  POST
    @RequestMapping(method = RequestMethod.POST)
    public Result save(@RequestBody Notice notice) {
        noticeService.save(notice);
        return new Result(true, StatusCode.OK, "新增成功");
    }

        
    // 4. 修改通知
    // http://127.0.0.1:9014/notice  PUT
    @RequestMapping(method = RequestMethod.PUT)
    public Result updateById(@RequestBody Notice notice) {
        noticeService.updateById(notice);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    // 5. 根据用户id查询该用户的待推送消息（新消息）
    // http://127.0.0.1:9014/notice/fresh/{userId}/{page}/{size}  GET
    @RequestMapping(value = "fresh/{userId}/{page}/{size}", method = RequestMethod.GET)
    public Result freshPage(@PathVariable String userId,
                            @PathVariable Integer page,
                            @PathVariable Integer size) {
        Page<NoticeFresh> pageData = noticeService.freshPage(userId,page,size);

        PageResult<NoticeFresh> pageResult = new PageResult<>(
                (int) pageData.getTotal(), pageData.getRecords()
        );

        return new Result(true, StatusCode.OK, "查询成功", pageResult);
    }

        
    // 6. 删除待推送消息（新消息）
    // http://127.0.0.1:9014/notice/fresh  DELETE
    @RequestMapping(value = "fresh",method = RequestMethod.DELETE)
    public Result freshDelete(@RequestBody NoticeFresh noticeFresh) {
        noticeService.freshDelete(noticeFresh);
        return new Result(true, StatusCode.OK, "删除成功");
    }
    
}
