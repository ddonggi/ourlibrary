package com.example.ourlibrary.books;

import com.example.ourlibrary.user.SiteUser;
import lombok.*;

import java.time.LocalDateTime;

/**
 * PackageName : com.example.ourlibrary.books
 * FileName : BookDTO
 * Author : dglee
 * Create : 3/3/24 8:28 PM
 * Description :
 **/

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookResponseDTO {

    private long id;
    private String bookName;
    private long ISBN;
    private int rentPrice;
    private boolean rentAvailable;
    private Integer rentCount;
    private LocalDateTime registrationDate;
    private SiteUser author;

    //책 리스트에서 사용하는 DTO 변환 함수
    public static BookResponseDTO toDTO(final Book book){
        return BookResponseDTO.builder()
                .id(book.getId())
                .bookName(book.getBookName())
                .ISBN(book.getISBN())
                .rentAvailable(book.getRentAvailable())
                .registrationDate(book.getRegistrationDate())
                .rentPrice(book.getRentPrice())
                .rentCount(book.getRentCount())
                .author(SiteUser.builder()
                        .id(book.getAuthor().getId())
                        .name(book.getAuthor().getName())
                        .build())
                .build();
    }
}
