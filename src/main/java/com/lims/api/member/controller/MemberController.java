package com.lims.api.member.controller;

import com.lims.api.member.dao.MemberDao;
import com.lims.api.member.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberDao memberDao;

    @GetMapping
    public ResponseEntity<List<MemberDto>> members() {
        return ResponseEntity.ok(memberDao.findAll("admin"));
    }


    @PutMapping
    public ResponseEntity<Integer> updateMember() {
        MemberDto dto = MemberDto.builder()
                .userId("adminUser")
                .loginId("admin")
                .build();
        return ResponseEntity.ok(memberDao.updateMember(dto));
    }

    @PatchMapping
    public ResponseEntity<Integer> updateMember2() {
        return ResponseEntity.ok(memberDao.updateMember2("admin"));
    }

}