package com.example.ourlibrary.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;

/**
 * PackageName : com.example.ourlibrary.config
 * FileName : SecurityConfig
 * Author : dglee
 * Create : 2/29/24 4:26 PM
 * Description :
 **/

@EnableMethodSecurity(prePostEnabled = true) //@PreAuthorize 애너테이션을 사용하기 위해 반드시 필요한 설정
@Configuration //스프링의 환경설정 파일임을 의미하는 애너테이션이다. 여기서는 스프링 시큐리티의 설정을 위해 사용되었다.
@EnableWebSecurity //모든 요청 URL이 스프링 시큐리티의 제어를 받도록 만드는 애너테이션이다. 이 애너테이션을 사용하면 스프링 시큐리티를 활성화하는 역할
@RequiredArgsConstructor
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return
                http
                        // 특정 URL에 대한 권한 설정.
                        .authorizeHttpRequests((authorizeRequests) -> {
                            authorizeRequests.requestMatchers(new AntPathRequestMatcher("/**"))
                                    .permitAll(); // 인증되지 않은 모든 url에 대해 승인
                        })
                        // Cors 정책 설정
                        .cors(corsCustomizer -> corsCustomizer.configurationSource(request -> {
                            CorsConfiguration config = new CorsConfiguration();
                            config.setAllowedMethods(Collections.singletonList("*"));
                            config.setAllowCredentials(true);
                            config.setMaxAge(3600L); //1시간
                            return config;
                        }))
                        // CSRF 에 대한 설정
                        .csrf((csrf) -> csrf
                                        .ignoringRequestMatchers(new AntPathRequestMatcher("/swagger-ui/**")) //Swagger API
                                        .ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**")) //DB
                                        .ignoringRequestMatchers(new AntPathRequestMatcher("/api/**")) //api
                        )
                        //클릭재킹 공격을 막기 위한 X-Frame-Options 헤더 설정
                        .headers((headers) -> headers
                                .addHeaderWriter(new XFrameOptionsHeaderWriter(
                                        XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
                        .formLogin(formLogin ->
                                /* 권한이 필요한 요청은 해당 url로 리다이렉트 */
                                formLogin
                                        .loginPage("/user/login")
                                        .usernameParameter("email")
                                        .defaultSuccessUrl("/")
                        )
                        .logout((logout) -> {
                            logout.logoutRequestMatcher(new AntPathRequestMatcher("/user/logout")) //로그아웃 url
                                    .logoutSuccessUrl("/")
                                    .invalidateHttpSession(true);// 사용자 세션 삭제
                        })

                        .build();

    }


    @Bean//비밀번호 암호화를 위한 BCryptPasswordEncoder 등록
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean //인증을 담당하는 AuthenticationManager Bean 생성
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
