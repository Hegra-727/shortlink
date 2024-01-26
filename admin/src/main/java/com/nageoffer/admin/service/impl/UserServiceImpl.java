package com.nageoffer.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.admin.dao.entity.UserDo;
import com.nageoffer.admin.dao.mapper.UserMapper;
import com.nageoffer.admin.dto.resp.UserRespDTO;
import com.nageoffer.admin.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDo> implements UserService {

    @Override
    public UserRespDTO getUserByUsername(String username) {
        LambdaQueryWrapper<UserDo> queryWrapper= Wrappers.lambdaQuery(UserDo.class)
                .eq(UserDo::getUsername,username);
        UserDo userDo=baseMapper.selectOne(queryWrapper);
        if (userDo==null)return null;
        UserRespDTO userRespDTO=new UserRespDTO();
        BeanUtils.copyProperties(userDo,userRespDTO);
        return userRespDTO;
    }
}
