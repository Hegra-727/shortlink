package com.nageoffer.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.admin.common.convention.exception.ClientException;
import com.nageoffer.admin.dao.entity.UserDo;
import com.nageoffer.admin.dao.mapper.UserMapper;
import com.nageoffer.admin.dto.req.UserRegisterReqDTO;
import com.nageoffer.admin.dto.resp.UserRespDTO;
import com.nageoffer.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import static com.nageoffer.admin.common.constant.RedisCacheConstant.LOCK_USER_REGISTER_KEY;
import static com.nageoffer.admin.common.enums.UserErrorCodeEnum.USER_NAME_EXIST;
import static com.nageoffer.admin.common.enums.UserErrorCodeEnum.USER_SAVE_ERROR;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDo> implements UserService {

    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;

    private final RedissonClient redissonClient;

    @Override
    public UserRespDTO getUserByUsername(String username) {
        LambdaQueryWrapper<UserDo> queryWrapper= Wrappers.lambdaQuery(UserDo.class)
                .eq(UserDo::getUsername,username);
        UserDo userDo=baseMapper.selectOne(queryWrapper);
        UserRespDTO userRespDTO=new UserRespDTO();
        BeanUtils.copyProperties(userDo,userRespDTO);
        return userRespDTO;
    }

    @Override
    public Boolean hasUserName(String username) {
        return !userRegisterCachePenetrationBloomFilter.contains(username);
    }

    @Override
    public void register(UserRegisterReqDTO requestParam) {
        if(!hasUserName(requestParam.getUsername())){
            throw new ClientException(USER_NAME_EXIST);
        }
        RLock lock=redissonClient.getLock(LOCK_USER_REGISTER_KEY+requestParam.getUsername());
        try {
            if(lock.tryLock()) {
                int inserted = baseMapper.insert(BeanUtil.toBean(requestParam, UserDo.class));
                if (inserted < 1) {
                    throw new ClientException(USER_SAVE_ERROR);
                }
                userRegisterCachePenetrationBloomFilter.add(requestParam.getUsername());
            }
            throw new ClientException(USER_NAME_EXIST);
        }finally {
            lock.unlock();
        }
    }
}
