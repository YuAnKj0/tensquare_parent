package com.tensquare.notice.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Ykj
 * @date 2022/11/6/19:34
 * @apiNote
 */
@Data
public class NoticeFresh implements Serializable {
    private String userId;
    private String noticeId;
}
