package com.nageoffer.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.admin.common.convention.exception.ClientException;
import com.nageoffer.admin.dao.entity.UserDO;
import com.nageoffer.admin.dao.mapper.UserMapper;
import com.nageoffer.admin.dto.req.UserLoginReqDTO;
import com.nageoffer.admin.dto.req.UserRegisterReqDTO;
import com.nageoffer.admin.dto.req.UserUpdateReqDTO;
import com.nageoffer.admin.dto.resp.UserLoginRespDTO;
import com.nageoffer.admin.dto.resp.UserRespDTO;
import com.nageoffer.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.nageoffer.admin.common.constant.RedisCacheConstant.LOCK_USER_REGISTER_KEY;
import static com.nageoffer.admin.common.constant.RedisCacheConstant.USER_LOGIN_KEY;
import static com.nageoffer.admin.common.enums.UserErrorCodeEnum.USER_NAME_EXIST;
import static com.nageoffer.admin.common.enums.UserErrorCodeEnum.USER_SAVE_ERROR;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;

    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public UserRespDTO getUserByUsername(String username) {
        LambdaQueryWrapper<UserDO> queryWrapper= Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername,username);
        UserDO userDo=baseMapper.selectOne(queryWrapper);
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
                int inserted = baseMapper.insert(BeanUtil.toBean(requestParam, UserDO.class));
                if (inserted < 1) {
                    throw new ClientException(USER_SAVE_ERROR);
                }
                userRegisterCachePenetrationBloomFilter.add(requestParam.getUsername());
            }
        }finally {
            lock.unlock();
        }
    }

    @Override
    public void update(UserUpdateReqDTO requestParam) {
        //TODO 验证当前登录用户
        LambdaUpdateWrapper<UserDO> wrapper = Wrappers.lambdaUpdate(UserDO.class)
                .eq(UserDO::getUsername, requestParam.getUsername());
        baseMapper.update(BeanUtil.toBean(requestParam, UserDO.class),wrapper);
    }

    @Override
    public UserLoginRespDTO login(UserLoginReqDTO requestParam) {
        //查询用户是否存在
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, requestParam.getUsername())
                .eq(UserDO::getPassword, requestParam.getPassword())
                .eq(UserDO::getDelFlag, 0);
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        if (userDO == null) {
            throw new ClientException("用户不存在");
        }
        //防止反复登录生成用户的多个hashKey:uuid(token)占用redis空间，复用token
        Map<Object, Object> hasLoginMap = stringRedisTemplate.opsForHash().entries(USER_LOGIN_KEY + requestParam.getUsername());
        if (CollUtil.isNotEmpty(hasLoginMap)) {
            stringRedisTemplate.expire(USER_LOGIN_KEY + requestParam.getUsername(), 30L, TimeUnit.MINUTES);
            String token = hasLoginMap.keySet().stream()
                    .findFirst()
                    .map(Object::toString)
                    .orElseThrow(() -> new ClientException("用户登录错误"));
            return new UserLoginRespDTO(token);
        }
        /**
         * Hash
         * Key：login_用户名
         * Value：
         *  Key：token标识
         *  Val：JSON 字符串（用户信息）
         */
        //常量前缀+用户名 作为用户唯一标识key，映射对应的hashKey:token value:JSON串
        String uuid = UUID.randomUUID().toString();
        stringRedisTemplate.opsForHash().put(USER_LOGIN_KEY + requestParam.getUsername(), uuid, JSON.toJSONString(userDO));
        stringRedisTemplate.expire(USER_LOGIN_KEY + requestParam.getUsername(), 30L, TimeUnit.MINUTES);
        return new UserLoginRespDTO(uuid);
    }

    @Override
    public Boolean checkLogin(String username,String token) {
        return stringRedisTemplate.opsForHash().get(username,token)!=null;
    }
}
