package com.gdgku.club;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * [문제: DTO 미분리]
 *
 * API 응답으로 Member/Club 엔티티를 그대로 돌려주고 있다. (생성, 조회 시 프론트엔드에 노출)
 * 할 일: 비밀번호를 제외한 응답 전용 DTO(예: MemberResponse)를 만들어서
 * Controller가 엔티티 대신 DTO를 반환하도록 리팩터링해서 테스트를 통과시키자.
 */
@RestController
@RequestMapping("/clubs")
public class ClubController {

    private final ClubService clubService;

    public ClubController(ClubService clubService) {
        this.clubService = clubService;
    }

    @PostMapping
    public Club createClub(@RequestBody Club request) {
        return clubService.createClub(request.getName());
    }

    @GetMapping("/{clubId}")
    public Club getClub(@PathVariable Long clubId) {
        return clubService.getClub(clubId);
    }

    @PostMapping("/{clubId}/members")
    public MemberResponse joinClub(@PathVariable Long clubId, @RequestBody Member request) {
        Member savedMember = clubService.addMember(clubId, request.getName(), request.getEmail(), request.getPassword());
        return MemberResponse.from(savedMember);
    }

    @GetMapping("/{clubId}/members")
        public List<MemberResponse> getMembers(@PathVariable Long clubId) {
        return clubService.getMembers(clubId).stream()
            .map(MemberResponse::from)
            .toList();
        }
}
