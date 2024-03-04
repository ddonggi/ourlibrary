package com.example.ourlibrary.books;

import com.example.ourlibrary.user.SiteUser;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

/**
 * PackageName : com.example.ourlibrary.books
 * FileName : Book
 * Author : dglee
 * Create : 3/2/24 10:26 PM
 * Description :
 **/

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@ToString
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //도서명
    @Column(nullable = false)
    private String bookName;

    @Column(nullable = false)
    private Long ISBN;

    //대여가능유무
    @ColumnDefault("true")
    private Boolean rentAvailable;

    //대여료
    @Column(nullable = false)
    private Integer rentPrice;

    //등록일
    private LocalDateTime registrationDate;

    //대여횟수
    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer rentCount;

    //위탁자
    @ManyToOne
    private SiteUser author;

}
