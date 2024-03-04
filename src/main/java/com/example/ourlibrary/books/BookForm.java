package com.example.ourlibrary.books;

import com.example.ourlibrary.user.SiteUser;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * PackageName : com.example.ourlibrary.books
 * FileName : BookForm
 * Author : dglee
 * Create : 2/29/24 8:45 PM
 * Description :
 **/

@ToString
@Getter
@Setter
public class BookForm {
    @NotEmpty(message = "책 제목을 입력해 주세요")
    private String bookName;

    @NotNull(message = "ISBN 번호를 입력해 주세요")
    @DecimalMin(value = "1000000000",message = "ISBN 은 최소 10자리 부터 작성해 주세요") //최소 10자리
    @DecimalMax(value = "9999999999999",message = "ISBN 은 최대 13자리 까지 작성해 주세요") //최대 13자리
    @JsonProperty
    private long ISBN;

    @NotNull(message = "대여 가격을 입력해 주세요")
    private int rentPrice;

    // 도서 등록시 사용하는 함수
    public Book toEntity(SiteUser siteUser){
        return Book.builder()
                .bookName(bookName)
                .ISBN(ISBN)
                .rentAvailable(true)
                .registrationDate(LocalDateTime.now())
                .rentCount(0)
                .rentPrice(rentPrice)
                .author(SiteUser.builder().id(siteUser.getId()).name(siteUser.getName()).build())
                .build();
    }
}
