package com.lims.api.member.service.impl;

import com.lims.api.member.dao.MemberDao;
import com.lims.api.member.dto.MemberDto;
import com.lims.api.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegularMemberService implements MemberService {

    private final MemberDao memberDao;

    @Override
    public int updateMember(MemberDto dto) {
        int result = memberDao.updateMember(dto);
        memberDao.updateMember2(dto);
        return result;
    }

    public int insertMember(MemberDto dto) {
        memberDao.insertMember(dto);
        dto.setUserId(102);
        memberDao.updateMember2(dto);
        return 1;
    }

    public int deleteMember(int id) {
        return memberDao.deleteMember(id);
    }

}