package com.nageoffer.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nageoffer.admin.dao.entity.UserDo;
import com.nageoffer.admin.dto.resp.UserRespDTO;

public interface UserService extends IService<UserDo> {
    UserRespDTO getUserByUsername(String username);
}
