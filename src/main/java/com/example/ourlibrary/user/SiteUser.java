package com.example.ourlibrary.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

/**
 * PackageName : com.example.ourlibrary.SiteUser
 * FileName : SiteUser
 * Author : dglee
 * Create : 2/29/24 4:22 PM
 * Description :
 **/

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SiteUser {

    //SiteUser로 하는 이유는 User 클래스가 이미 스프링 시큐리티에 있기 때문에, 혼선을 막기 위함
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    @Email
    private String email;

    private String phone1;
    private String phone2;
    private String phone3;

    private String password;

    public SiteUser(String name, String email, String phone1, String phone2, String phone3, String password) {
        this.name = name;
        this.email = email;
        this.phone1 = phone1;
        this.phone2 = phone2;
        this.phone3 = phone3;
        this.password = password;
    }

    @Override
    public String toString() {
        return "SiteUser{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone1='" + phone1 + '\'' +
                ", phone2='" + phone2 + '\'' +
                ", phone3='" + phone3 + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
