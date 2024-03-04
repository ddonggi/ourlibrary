package com.example.ourlibrary.user;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * PackageName : com.example.ourlibrary.user
 * FileName : UserCreateForm
 * Author : dglee
 * Create : 2/29/24 8:41 PM
 * Description : 회원가입 검증을 위한 객체
 **/

@ToString
@Getter
@Setter
@Builder
public class UserCreateForm {
    @NotEmpty(message = "닉네임은 필수 항목 입니다.알맞은 한글 이름으로 작성해 주세요.")
    @NotBlank(message = "빈칸 없이 알맞은 한글 이름으로 작성해 주세요")
    @Pattern(regexp = "^[가-힣]+$",message = "알맞은 한글 이름으로 작성해 주세요")
    private String name;

    @NotEmpty(message = "이메일은 필수 항목 입니다")
    @Email(message = "이메일 양식에 맞지 않습니다")
    @NotBlank(message = "빈칸 없이 작성해 주세요")
    @Pattern(regexp = "[a-z0-9]+@[a-z]+.[a-z]{2,3}",message = "이메일 양식에 맞춰주세요")
    private String email;

    @Size(min = 10, max = 11)
    @NotEmpty(message = "전화번호는 필수 항목 입니다")
    @NotBlank(message = "빈칸 없이 작성해 주세요")
    @Pattern(regexp = "[0-9]{10,11}", message = "10~11자리의 숫자만 입력가능합니다")
    private String phoneNumber;

    @Size(min = 6, max = 10)
    @NotBlank(message = "빈칸 없이 작성해 주세요")
    @NotEmpty(message = "비밀번호는 필수 항목 입니다")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[0-9])[A-Z0-9]{6,10}|(?=.*[a-z])(?=.*[0-9])[a-z0-9]{6,10}|(?=.*[A-Z])(?=.*[a-z])[A-Za-z]{6,10}|(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])[A-Za-z0-9]{6,10}$",message = "영문 소문자, 대문자, 숫자 중 최소 두 가지 이상을 포함해야 합니다")
    private String password;

    public SiteUser toEntity(){
        String[] phones = parsePhone();
        return SiteUser.builder()
                .name(name)
                .email(email)
                .phone1(phones[0])
                .phone2(phones[1])
                .phone3(phones[2])
                .password(password)
                .build();
    }

    private String[] parsePhone(){
        String[] phones = new String[3];
        int mid = this.phoneNumber.length() == 10? 6:7;//10자리 이면 7
        phones[0] = this.phoneNumber.substring(0,3);
        phones[1] = this.phoneNumber.substring(3,mid);
        phones[2] = this.phoneNumber.substring(mid,this.phoneNumber.length());
        return phones;
    }

}
