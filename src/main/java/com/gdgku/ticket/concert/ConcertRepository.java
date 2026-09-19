package com.gdgku.ticket.concert;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ConcertRepository extends JpaRepository<Concert, Long> {

    // 이 concert row를 조회하면서 즉시 DB 락을 건다.
    // 다른 트랜잭션이 같은 row를 이 메서드로 조회하려 하면,
    // 이 트랜잭션이 끝날 때까지 대기한다.
    // 그래서 "재고 확인 -> 차감" 사이에 다른 요청이 끼어들 수 없게 된다.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Concert c WHERE c.id = :id")
    Optional<Concert> findByIdForUpdate(@Param("id") Long id);
}