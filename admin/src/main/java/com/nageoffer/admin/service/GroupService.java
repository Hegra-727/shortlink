package com.nageoffer.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nageoffer.admin.dao.entity.GroupDO;
import com.nageoffer.admin.dto.req.ShortLinkGroupSortReqDTO;
import com.nageoffer.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import com.nageoffer.admin.dto.resp.ShortLinkGroupRespDTO;

import java.util.List;

public interface GroupService extends IService<GroupDO> {
    void saveGroup(String groupName);

    List<ShortLinkGroupRespDTO> listGroup();


    void updateGroup(ShortLinkGroupUpdateReqDTO requestParam);

    void deleteGroup(String gid);

    void sortGroup(List<ShortLinkGroupSortReqDTO> requestParam);
}
