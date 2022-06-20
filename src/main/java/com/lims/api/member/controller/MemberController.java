package com.lims.api.member.controller;

import com.lims.api.member.dao.MemberDao;
import com.lims.api.member.dto.MemberDto;
import com.lims.api.member.service.MemberService;
import com.lims.api.member.service.impl.RegularMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberDao memberDao;
    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<List<MemberDto>> members() {
        return ResponseEntity.ok(memberDao.findAll("admin"));
    }


    @PutMapping
    public ResponseEntity<Integer> updateMember() {
        MemberDto dto = MemberDto.builder()
                .userId(102)
                .loginId("admin")
                .build();
        return ResponseEntity.ok(memberService.updateMember(dto));
    }

    @PatchMapping
    public ResponseEntity<Integer> updateMember2() {
        return ResponseEntity.ok(memberDao.updateMember2("admin"));
    }

    @DeleteMapping
    public ResponseEntity<Integer> deleteMember() {
        return ResponseEntity.ok(memberDao.deleteMember());
    }

}