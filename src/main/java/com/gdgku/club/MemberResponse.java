package com.gdgku.club;

/**
 * 회원 정보 응답 전용 DTO.
 * Member 엔티티를 그대로 반환하지 않고, 비밀번호를 제외한 필드만 노출한다.
 */
public class MemberResponse {

    private final Long id;
    private final String name;
    private final String email;

    private MemberResponse(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // Entity -> DTO 변환을 DTO 쪽에 정적 팩토리로 두면
    // Controller/Service가 변환 로직을 몰라도 되어 관심사가 분리된다.
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getName(),
                member.getEmail()
        );
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}