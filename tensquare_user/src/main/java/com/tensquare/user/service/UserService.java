package com.tensquare.user.service;

import com.tensquare.user.dao.UserDao;
import com.tensquare.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Ykj
 * @date 2022/11/6/11:57
 * @apiNote
 */
@Service
public class UserService {
    
    @Autowired
    private UserDao userDao;
    
    public User login(User user) {
        return userDao.selectOne(user.getId());
    }
    
    public User selectById(String id) {
        
        return userDao.selectById(id);
    }
}
