package com.gdgku.library;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LibraryService {

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;

    public LibraryService(
            BookRepository bookRepository,
            LoanRepository loanRepository
    ) {
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
    }

    public Book registerBook(String title, int totalCopies) {
        Book book = new Book(null, title, totalCopies, totalCopies);
        return bookRepository.save(book);
    }

    public Book getBook(Long bookId) {
    return bookRepository.findById(bookId)
                .orElse(null);
    }

    @Transactional
    public Loan borrow(Long bookId, String borrowerName) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 도서입니다."));
        
            if (book.getAvailableCopies() <= 0) {
            throw new IllegalStateException("대출 가능한 재고가 없습니다.");
        }

        // 1단계: 재고 차감. 트랜잭션이 없어서 2단계가 실패해도 이 변경은 롤백되지 않는다.
        book.setAvailableCopies(book.getAvailableCopies() - 1);

        // 2단계: 이미 같은 책을 빌리고 아직 반납하지 않았는지는 재고를 깎은 "뒤에" 확인한다.
        if (loanRepository.existsByBookIdAndBorrowerNameAndReturnedFalse(
                bookId, borrowerName)) {
            throw new IllegalStateException(
                    borrowerName + "님은 이미 이 책을 대출 중입니다."
            );
        }

        Loan loan = new Loan(null, bookId, borrowerName, false);
        return loanRepository.save(loan);
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    @Transactional 
    public Loan returnBook(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElse(null);

        if (loan == null) {
            return null;
        }

        loan.setReturned(true);

        Book book = bookRepository.findById(loan.getBookId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 도서입니다."));

        book.setAvailableCopies(book.getAvailableCopies() + 1);

        return loan;
    }
}
