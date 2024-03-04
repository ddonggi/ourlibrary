package com.example.ourlibrary.books;

import com.example.ourlibrary.user.SiteUser;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * PackageName : com.example.ourlibrary.books
 * FileName : Rental
 * Author : dglee
 * Create : 3/3/24 8:05 PM
 * Description :
 **/

@Entity
@Getter
@Builder
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private LocalDateTime rentalDate;

    private LocalDateTime limitDate;

    private LocalDateTime returnDate;

    @ManyToOne
    private Book book;//대여 책
    @ManyToOne
    private SiteUser borrower;//대여자

}
