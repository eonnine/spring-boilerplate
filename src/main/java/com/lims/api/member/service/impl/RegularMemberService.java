package com.lims.api.member.service.impl;

import com.lims.api.member.dao.MemberDao;
import com.lims.api.member.dto.MemberDto;
import com.lims.api.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class RegularMemberService implements MemberService {

    private final MemberDao memberDao;

    @Override
    public int updateMember(MemberDto dto) {
        int result = memberDao.updateMember(dto);
        deleteMember();
        return result;
    }

    public int deleteMember() {
        System.out.println(TransactionSynchronizationManager.isSynchronizationActive());
        System.out.println(TransactionSynchronizationManager.isActualTransactionActive());
        System.out.println(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        return memberDao.deleteMember();
    }

}
