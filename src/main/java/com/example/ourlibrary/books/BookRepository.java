package com.example.ourlibrary.books;

import com.example.ourlibrary.user.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<List<Book>> findAllByBookName(String bookName);

//    Page<Book> findAll(Pageable pageable);
//    Page<Book> countByAuthorAndBookAndId(SiteUser author, String bookId);
}
