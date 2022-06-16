package com.lims.api.member.repository;

import com.lims.api.common.annotation.Audit;
import com.lims.api.member.dto.MemberDto;

import java.util.List;

public interface MemberDao {

    List<MemberDto> findAll(String id);

    MemberDto findByLoginId(String id);

    @Audit(query = "select * from sy_user where login_id = ?", bindParameter = {"userId"})
    int updateMember(String id);

    @Audit(method = "MemberDao.findByLoginId", bindParameter = {"userId"})
    int updateMember2(String id);

}