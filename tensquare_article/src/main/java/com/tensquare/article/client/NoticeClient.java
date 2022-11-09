package com.tensquare.article.client;

import com.tensquare.article.vo.NoticeVo;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Ykj
 * @date 2022/11/8/22:46
 * @apiNote
 */
@FeignClient("tensquare-notice")
public interface NoticeClient {
    
    /**
     * 添加消息
     * @param notice
     * @return
     */
    @RequestMapping(value = "notice",method = RequestMethod.POST)
    public Result add(@RequestBody NoticeVo notice) ;
}
