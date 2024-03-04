package com.example.ourlibrary.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * PackageName : com.example.ourlibrary.user
 * FileName : UserSecurityService
 * Author : dglee
 * Create : 3/3/24 12:26 AM
 * Description :
 **/

@RequiredArgsConstructor
@Service
@Slf4j
public class UserSecurityService implements UserDetailsService {
    private final SiteUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("email:{}",email);
        Optional<SiteUser> optionalSiteUser = userRepository.findByEmail(email); //기존 유저 이름에서 이메일로 로그인
        if(optionalSiteUser.isEmpty()){
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
        SiteUser siteUser = optionalSiteUser.get();
        List<GrantedAuthority> authorities = new ArrayList<>();
        log.info("email:{}",email);
        if ("admin@admin.com".equals(email)) {
            log.info("관리자 유저 계정 입니다");
            authorities.add(new SimpleGrantedAuthority(UserRole.ADMIN.getValue()));
        } else {
            log.info("일반 유저 계정 입니다");
            authorities.add(new SimpleGrantedAuthority(UserRole.USER.getValue()));
        }
        return new User(siteUser.getEmail(), siteUser.getPassword(), authorities);
    }
}
