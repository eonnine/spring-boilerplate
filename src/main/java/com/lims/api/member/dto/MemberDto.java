package com.lims.api.member.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto extends AuditParamDto {

    private Integer userId;
    private String loginId;
    private String deptCode;
    private String userNm;
    private String lockAt;
    private String useAt;
    private String lastChangerId;

    private final String menuCd = "M0001";
    private final int menuId = 100;

}