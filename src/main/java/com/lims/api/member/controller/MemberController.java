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
    public ResponseEntity<Integer> updateMember(MemberDto dto) {
        int r = memberService.updateMember(dto);
        return ResponseEntity.ok(r);
    }

    @PostMapping
    public ResponseEntity<Integer> insertMember(MemberDto dto) {
        int r = memberService.insertMember(dto);
        return ResponseEntity.ok(r);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Integer> deleteMember(@PathVariable int id) {
        int r = memberService.deleteMember(id);
        return ResponseEntity.ok(r);
    }

}