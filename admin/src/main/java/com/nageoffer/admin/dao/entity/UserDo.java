package com.nageoffer.admin.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_user")
public class UserDo {
    private String id;
    private String username;
    private String password;
    private String real_name;
    private String phone;
    private String mail;
    private String deletion_time;
    private String create_time;
    private String update_time;
    private String del_flag;
}
