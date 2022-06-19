package com.lims.api.member.dao;

import com.lims.api.audit.annotation.Audit;
import com.lims.api.member.dto.MemberDto;
import com.lims.api.member.entity.Member;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberDao {

    List<MemberDto> findAll(String id);

    MemberDto findByLoginId(String id);

    @Audit(target = Member.class)
    int updateMember(MemberDto dto);

    @Audit(target = Member.class)
    Integer updateMember2(String id);

}