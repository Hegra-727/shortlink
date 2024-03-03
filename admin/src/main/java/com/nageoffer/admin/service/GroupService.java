package com.nageoffer.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nageoffer.admin.dao.entity.GroupDO;
import com.nageoffer.admin.dto.resp.ShortLinkGroupRespDTO;

import java.util.List;

public interface GroupService extends IService<GroupDO> {
    void saveGroup(String groupName);

    List<ShortLinkGroupRespDTO> listGroup();
}
