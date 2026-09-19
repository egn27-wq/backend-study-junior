package com.gdgku.library;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    boolean existsByBookIdAndBorrowerNameAndReturnedFalse(
        Long bookId,
        String borrowerName
);
}

