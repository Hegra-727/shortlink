package com.nageoffer.admin.dto.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nageoffer.admin.common.serialize.PhoneDesensitizationSerializer;
import lombok.Data;

@Data
public class UserRespDTO {
    private String id;
    private String username;
    private String realName;
    @JsonSerialize(using = PhoneDesensitizationSerializer.class)
    private String phone;
    private String mail;
}
