package com.tensquare.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tensquare.user.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author Ykj
 * @date 2022/11/6/11:56
 * @apiNote
 */
public interface UserDao extends BaseMapper<User> {
    @Select("select * from tb_user where id=#{userId}")
    User selectOne( @Param("userId") String userId);
}
