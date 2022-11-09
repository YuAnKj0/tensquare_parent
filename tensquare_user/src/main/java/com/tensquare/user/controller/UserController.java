package com.tensquare.user.controller;

import com.tensquare.user.pojo.User;
import com.tensquare.user.service.UserService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Ykj
 * @date 2022/11/6/11:58
 * @apiNote
 */

@RestController
@RequestMapping("user")
@CrossOrigin
public class UserController {
    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    public Result login(@RequestBody User user){
        User result=userService.login(user);
        if (null!=result) {
            return new Result(true, StatusCode.OK,"登录成功",result);
        }
        return new Result(false,StatusCode.LOGINERROR,"登录失败");        
    }
    @GetMapping("/{id}")
    public Result selectById(@PathVariable("id") String id){
        User user = userService.selectById(id);
        return new Result(true, StatusCode.OK, "查询成功", user);
    
    }}
