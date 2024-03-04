package com.example.ourlibrary.user;

import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * PackageName : com.example.ourlibrary.user
 * FileName : SiteUserService
 * Author : dglee
 * Create : 3/1/24 11:13 PM
 * Description :
 **/

@Service
@RequiredArgsConstructor //final이 붙거나 @NotNull 이 붙은 필드의 생성자를 자동 생성해주는 롬복 어노테이션
public class SiteUserService {
    private final SiteUserRepository siteUserRepository;
    private final PasswordEncoder passwordEncoder;

    public SiteUser create(UserCreateForm userCreateForm){
        //비밀번호 암호화
        userCreateForm.setPassword(passwordEncoder.encode(userCreateForm.getPassword()));
        //DTO
        return siteUserRepository.save(userCreateForm.toEntity());
    }

    public boolean emailExist(String email) {
        Optional<SiteUser> optionalUser = siteUserRepository.findByEmail(email);
        return optionalUser.isPresent();
    }

    public List<SiteUser> findAll() {
        return siteUserRepository.findAll();
    }

    public SiteUser findByEmail(String email){
        Optional<SiteUser> optionalSiteUser = siteUserRepository.findByEmail(email);
        return optionalSiteUser.orElse(null);
    }
    public SiteUser findById(long id){
        Optional<SiteUser> optionalSiteUser = siteUserRepository.findById(id);
        return optionalSiteUser.orElse(null);
    }
}
