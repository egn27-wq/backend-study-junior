package com.gdgku.club;

import java.sql.*;

public class ClubCrud {
    // [변경 1] static 제거 → 객체마다 따로 갖는 값(공책)이 됨
    private final String url;
    private final String user;
    private final String password;

    // [변경 2] 생성자 추가 → 만들 때(new) DB 정보를 받아야만 객체가 생김
    // = ClubCrud는 DB 정보에 "종속"된다
    public ClubCrud(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    // static 제거
    Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    // C: 추가 (static 제거)
    long createClub(String name) throws SQLException {
        String sql = "INSERT INTO club (name) VALUES (?) RETURNING id";
        try (Connection conn = connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong("id");
            }
        }
    }

    // R: 조회 (static 제거)
    void findAllClubs() throws SQLException {
        String sql = "SELECT id, name FROM club ORDER BY id";
        try (Connection conn = connect();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            System.out.println("--- club list ---");
            while (rs.next()) {
                System.out.println(rs.getLong("id") + " | " + rs.getString("name"));
            }
        }
    }

    // U: 수정 (static 제거)
    int updateClub(long id, String newName) throws SQLException {
        String sql = "UPDATE club SET name = ? WHERE id = ?";
        try (Connection conn = connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newName);
            ps.setLong(2, id);
            return ps.executeUpdate();
        }
    }

    // D: 삭제 (static 제거)
    int deleteClub(long id) throws SQLException {
        String sql = "DELETE FROM club WHERE id = ?";
        try (Connection conn = connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate();
        }
    }

    // club 한 개 조회 (id로) (static 제거)
    void findClubById(long id) throws SQLException {
        String sql = "SELECT id, name FROM club WHERE id = ?";
        try (Connection conn = connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("club: " + rs.getLong("id") + " | " + rs.getString("name"));
                } else {
                    System.out.println("club " + id + " not found");
                }
            }
        }
    }

    // member 추가 → 새 member id 반환 (static 제거)
    long createMember(long clubId, String name, String email, String password) throws SQLException {
        String sql = "INSERT INTO member (club_id, name, email, password) VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection conn = connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, clubId);
            ps.setString(2, name);
            ps.setString(3, email);
            ps.setString(4, password);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong("id");
            }
        }
    }

    // 특정 club의 member 목록 조회 (club_id로) (static 제거)
    void findMembersByClubId(long clubId) throws SQLException {
        String sql = "SELECT id, name, email FROM member WHERE club_id = ? ORDER BY id";
        try (Connection conn = connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, clubId);
            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("--- members of club " + clubId + " ---");
                while (rs.next()) {
                    System.out.println(rs.getLong("id") + " | " + rs.getString("name") + " | " + rs.getString("email"));
                }
            }
        }
    }

    // main은 프로그램 시작점이라 static 그대로 둠
    public static void main(String[] args) throws SQLException {
        // [변경 3] new로 객체를 만들면서 DB 정보를 넣어줌 (충전기 꽂기)
        ClubCrud crud = new ClubCrud(
                "jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");

        // long id = crud.createClub("Book Club");
        // System.out.println("created id = " + id);
        // crud.findAllClubs();

        // System.out.println("updated rows = " + crud.updateClub(id, "Reading Club"));
        // crud.findAllClubs();

        // System.out.println("deleted rows = " + crud.deleteClub(id));

        // [변경 4] 앞에 crud. 을 붙여서 호출
        crud.findClubById(2);
        long memberId = crud.createMember(2, "홍길동", "hong@gdgku.com", "plainPassword123");
        System.out.println("created member id = " + memberId);
        crud.findMembersByClubId(2);
    }
}