package com.lims.api.member.entity;

import com.lims.api.audit.annotation.AuditEntity;
import com.lims.api.audit.annotation.AuditId;
import lombok.Builder;
import lombok.Getter;

@AuditEntity(name = "SY_USER")
public class Member {

    @AuditId
    private String userId;

    @AuditId
    private String loginId;

    private String userNm;
    private String password;

}