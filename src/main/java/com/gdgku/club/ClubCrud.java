package com.gdgku.club;

import java.sql.*;

public class ClubCrud {
    static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    static final String USER = "postgres";
    static final String PASSWORD = "postgres";

    static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // C: 추가
    static long createClub(String name) throws SQLException {
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

    // R: 조회
    static void findAllClubs() throws SQLException {
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

    // U: 수정
    static int updateClub(long id, String newName) throws SQLException {
        String sql = "UPDATE club SET name = ? WHERE id = ?";
        try (Connection conn = connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newName);
            ps.setLong(2, id);
            return ps.executeUpdate();
        }
    }

    // D: 삭제
    static int deleteClub(long id) throws SQLException {
        String sql = "DELETE FROM club WHERE id = ?";
        try (Connection conn = connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate();
        }
    }
    
    // club 한 개 조회 (id로)
    static void findClubById(long id) throws SQLException {
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

    // member 추가 → 새 member id 반환
    static long createMember(long clubId, String name, String email, String password) throws SQLException {
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

    // 특정 club의 member 목록 조회 (club_id로)
    static void findMembersByClubId(long clubId) throws SQLException {
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

    public static void main(String[] args) throws SQLException {
        // long id = createClub("Book Club");
        // System.out.println("created id = " + id);
        // findAllClubs();

        // System.out.println("updated rows = " + updateClub(id, "Reading Club"));
        // findAllClubs();

        // System.out.println("deleted rows = " + deleteClub(id));
        findClubById(2);
        long memberId = createMember(2, "홍길동", "hong@gdgku.com", "plainPassword123");
        System.out.println("created member id = " + memberId);
        findMembersByClubId(2);
    }
}
