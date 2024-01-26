package com.nageoffer.admin.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_user")
public class UserDo {
    private String id;
    private String username;
    private String password;
    private String realName;
    private String phone;
    private String mail;
    private String deletionTime;
    private String createTime;
    private String updateTime;
    private String delFlag;
}
