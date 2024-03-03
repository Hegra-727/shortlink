package com.nageoffer.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.admin.dao.entity.GroupDO;
import com.nageoffer.admin.dao.mapper.GroupMapper;
import com.nageoffer.admin.service.GroupService;
import com.nageoffer.admin.toolkit.RandomGenerator;
import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {
    @Override
    public void saveGroup(String groupName) {
        String gid;
        do{
            gid= RandomGenerator.generateRandom();
        } while (!hasGid(gid));
        GroupDO groupDO=GroupDO.builder()
                .gid(RandomGenerator.generateRandom())
                .sortOrder(0)
                .name(groupName)
                .build();
        baseMapper.insert(groupDO);
    }
    public boolean hasGid(String gid){
        LambdaQueryWrapper<GroupDO> queryWrapper= Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid,gid)
                //TODO 设置用户名
                .eq(GroupDO::getUsername,null);
        GroupDO hasGroupFlag=baseMapper.selectOne(queryWrapper);
        return hasGroupFlag==null;
    }
}
