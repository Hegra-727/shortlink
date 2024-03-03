package com.nageoffer.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nageoffer.admin.dao.entity.GroupDO;

public interface GroupService extends IService<GroupDO> {
    void saveGroup(String groupName);
}
