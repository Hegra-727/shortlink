package com.nageoffer.admin.controller;

import com.nageoffer.admin.common.convention.result.Result;
import com.nageoffer.admin.common.convention.result.Results;
import com.nageoffer.admin.dto.req.ShortLinkGroupSaveReqDTO;
import com.nageoffer.admin.dto.req.ShortLinkGroupSortReqDTO;
import com.nageoffer.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import com.nageoffer.admin.dto.resp.ShortLinkGroupRespDTO;
import com.nageoffer.admin.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    /**
     * 增加短链接分组集合
     * @param requestParam
     * @return
     */
    @PostMapping("/api/short-link/admin/v1/group")
    public Result<Void> save(@RequestBody ShortLinkGroupSaveReqDTO requestParam){
        groupService.saveGroup(requestParam.getName());
        return Results.success();
    }

    /**
     * 查询短链接分组集合
     * @return
     */
    @GetMapping("/api/short-link/admin/v1/group")
    public Result<List<ShortLinkGroupRespDTO>> listGroup(){
        return Results.success(groupService.listGroup());
    }

    /**
     * 修改短链接分组集合
     * @param requestParam
     * @return
     */
    @PutMapping("/api/short-link/admin/v1/group")
    public Result<Void> updateGroup(@RequestBody ShortLinkGroupUpdateReqDTO requestParam){
        groupService.updateGroup(requestParam);
        return Results.success();
    }

    @DeleteMapping("/api/short-link/admin/v1/group")
    public Result<Void> updateGroup(@RequestParam String gid){
        groupService.deleteGroup(gid);
        return Results.success();
    }

    @PostMapping("/api/short-link/admin/v1/sort")
    public Result<Void> sortGroup(@RequestBody List<ShortLinkGroupSortReqDTO> requestParam){
        groupService.sortGroup(requestParam);
        return Results.success();
    }
}
