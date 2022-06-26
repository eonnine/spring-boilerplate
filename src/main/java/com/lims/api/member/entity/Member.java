package com.lims.api.member.entity;

import com.lims.api.audit.annotation.AuditEntity;
import com.lims.api.audit.annotation.AuditId;

@AuditEntity(name = "SY_USER")
public class Member {

    @AuditId
    private String userId;

    private String loginId;

    private String userNm;
    private String password;
    private String lastChangeDt;
    private String lastChangerId;

}