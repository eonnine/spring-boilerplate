package com.lims.api.member.service;

import com.lims.api.member.dto.MemberDto;

public interface MemberService {

    int updateMember(MemberDto dto);

    int insertMember(MemberDto dto);

    int deleteMember(int id);

}
