package com.lims.api.member.controller;

import com.lims.api.member.repository.MemberDao;
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
        return ResponseEntity.ok(memberDao.updateMember("admin"));
    }

    @PatchMapping
    public ResponseEntity<Integer> updateMember2() {
        return ResponseEntity.ok(memberDao.updateMember2("admin"));
    }

}