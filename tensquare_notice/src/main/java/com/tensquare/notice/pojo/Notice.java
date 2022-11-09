package com.tensquare.notice.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Ykj
 * @date 2022/11/6/19:26
 * @apiNote
 */
@Data
@TableName("tb_notice")
public class Notice implements Serializable {
    
    @TableId(type = IdType.INPUT)
    private String id;//ID

    private String receiverId;//接收消息的用户ID
    private String operatorId;//进行操作的用户ID

    @TableField(exist = false)
    private String operatorName;//进行操作的用户昵称
    private String action;//操作类型（评论，点赞等）
    private String targetType;//对象类型（评论，点赞等）

    @TableField(exist = false)
    private String targetName;//对象名称或简介
    private String targetId;//对象id
    private Date createtime;//创建日期
    private String type;    //消息类型
    private String state;   //消息状态（0 未读，1 已读）
}
