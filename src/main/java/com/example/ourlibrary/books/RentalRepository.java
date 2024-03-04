package com.example.ourlibrary.books;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findAllByBorrowerId(long borrowerId);//빌린사람의 모든 대여 목록
    //빌린사람의 현재 기준 대여일자 및 반납기한 사이인 대여 목록
    List<Rental> findByBorrowerIdAndRentalDateBetweenAndLimitDateAfter(Long borrower_id, LocalDateTime rentalDate, LocalDateTime now, LocalDateTime limitDate);
    List<Rental> findByBorrowerIdAndRentalDateBetween(Long borrower_id, LocalDateTime rentalDate, LocalDateTime limitDate);
    Rental findByBorrowerIdAndBookIdAndRentalDateBetween(Long borrower_id, Long book_id, LocalDateTime rentalDate, LocalDateTime rentalDate2);
    Rental findRentalByBookIdAndBorrowerId(long bookId, long borrowerId);
}
