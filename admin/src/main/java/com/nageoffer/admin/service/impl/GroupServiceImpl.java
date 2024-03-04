package com.nageoffer.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.admin.common.biz.user.UserContext;
import com.nageoffer.admin.dao.entity.GroupDO;
import com.nageoffer.admin.dao.mapper.GroupMapper;
import com.nageoffer.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import com.nageoffer.admin.dto.resp.ShortLinkGroupRespDTO;
import com.nageoffer.admin.service.GroupService;
import com.nageoffer.admin.toolkit.RandomGenerator;
import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {
    @Override
    public void saveGroup(String groupName) {
        System.out.println("上下文用户名username："+UserContext.getUsername());
        String gid;
        do{
            gid= RandomGenerator.generateRandom();
        } while (!hasGid(gid));
        GroupDO groupDO=GroupDO.builder()
                .gid(RandomGenerator.generateRandom())
                .sortOrder(0)
                .username(UserContext.getUsername())
                .name(groupName)
                .build();
        baseMapper.insert(groupDO);
    }

    @Override
    public List<ShortLinkGroupRespDTO> listGroup() {
        LambdaQueryWrapper<GroupDO> queryWrapper=Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getDelFlag,0)
                //TODO 从当前上下文获取用户名
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .orderByDesc(GroupDO::getSortOrder,GroupDO::getUpdateTime);
        List<GroupDO> groupDOList=baseMapper.selectList(queryWrapper);
        return BeanUtil.copyToList(groupDOList, ShortLinkGroupRespDTO.class);
    }

    @Override
    public void updateGroup(ShortLinkGroupUpdateReqDTO requestParam) {
        LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getGid, requestParam.getGid())
                .eq(GroupDO::getDelFlag, 0);
        GroupDO groupDO=new GroupDO();
        groupDO.setName(requestParam.getName());
        baseMapper.update(groupDO,updateWrapper);
    }

    public boolean hasGid(String gid){
        LambdaQueryWrapper<GroupDO> queryWrapper= Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid,gid)
                //TODO 设置用户名
                .eq(GroupDO::getUsername,UserContext.getUsername());
        GroupDO hasGroupFlag=baseMapper.selectOne(queryWrapper);
        return hasGroupFlag==null;
    }
}
