package com.tensquare.notice.client;

import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Ykj
 * @date 2022/11/7/15:05
 * @apiNote
 */
@FeignClient(value = "tensquare-article")
public interface ArticleClient {
    /**
     * 根据ID查询文章
     *
     * @param articleId
     * @return
     */
    @RequestMapping(value = "/article/{articleId}", method = RequestMethod.GET)
    public Result findById(@PathVariable("articleId") String articleId);
    
  
}
