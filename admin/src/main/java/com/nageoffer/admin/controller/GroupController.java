package com.nageoffer.admin.controller;

import com.nageoffer.admin.common.convention.result.Result;
import com.nageoffer.admin.common.convention.result.Results;
import com.nageoffer.admin.dto.req.ShortLinkGroupSaveReqDTO;
import com.nageoffer.admin.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
    @PostMapping("/api/short-link/admin/v1/group")
    public Result<Void> save(@RequestBody ShortLinkGroupSaveReqDTO requestParam){
        groupService.saveGroup(requestParam.getName());
        return Results.success();
    }
}
