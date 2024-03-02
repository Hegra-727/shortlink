package com.nageoffer.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nageoffer.admin.dao.entity.UserDo;
import com.nageoffer.admin.dto.req.UserRegisterReqDTO;
import com.nageoffer.admin.dto.req.UserUpdateReqDTO;
import com.nageoffer.admin.dto.resp.UserRespDTO;

public interface UserService extends IService<UserDo> {
    UserRespDTO getUserByUsername(String username);

    Boolean hasUserName(String username);

    /**
     * 注册用户
     * @param requestParam 注册用户请求参数
     */
    void register(UserRegisterReqDTO requestParam);

    void update(UserUpdateReqDTO requestParam);
}
