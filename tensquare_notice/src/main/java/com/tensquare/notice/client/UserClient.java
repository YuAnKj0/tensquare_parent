package com.tensquare.notice.client;

import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Ykj
 * @date 2022/11/7/15:06
 * @apiNote
 */
@FeignClient(value = "tensquare-user")
public interface UserClient {
    /**
     *  根据ID查询用户
     * @param id
     * @return
     */
    @RequestMapping(value="/user/{id}",method = RequestMethod.GET)
    public Result findById(@PathVariable("id") String id);
}
