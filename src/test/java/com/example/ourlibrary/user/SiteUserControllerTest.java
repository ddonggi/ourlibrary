package com.example.ourlibrary.user;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * PackageName : com.example.ourlibrary.user
 * FileName : SiteUserControllerTest
 * Author : dglee
 * Create : 3/1/24 11:36 PM
 * Description :
 **/

@WebMvcTest(SiteUserController.class)//테스트할 특정 컨트롤러명
@WithMockUser
class SiteUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    //@WebMvcTest 어노테이션은 Web 레이어 관련 Bean들만 등록하기 때문에 Service가 등록되지 않기에 MockBean으로 가짜 객체를 등록해준다.
    @MockBean
    private SiteUserService siteUserService;

    @DisplayName("회원가입 : 이름 형식에 맞지 않을때")
    @Test
    void signUpNameFail() throws Exception{
        //given
        String name = "ㅇㄹㅁㄴ";
        String password = "kkdfdfdf";
        String phoneNumber = "01055370157";
        String email="ldg6153@email.com";
        UserCreateForm userCreateForm = UserCreateForm.builder()
                .name(name).email(email).phoneNumber(phoneNumber).password(password).build();

        Gson gson = new Gson();
        String form = gson.toJson(userCreateForm);

        MvcResult result = mockMvc.perform(
                        post("/api/v1/user/signup")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(form)
                                .characterEncoding("UTF-8")
                )
                .andExpect(status().isOk())
                .andReturn();

        /* get result  */
        String response = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("response = " + response);

    }
    @DisplayName("회원가입 : 모든 폼 ok")
    @Test
    void signUpMVCTest() throws Exception{
        //given
        String name = "이동기";
        String password = "kkwe1068";
        String phoneNumber = "01055370157";
        String email="ldg6153@email.com";
        UserCreateForm userCreateForm = UserCreateForm.builder()
                .name(name).email(email).phoneNumber(phoneNumber).password(password).build();


        Gson gson = new Gson();
        String form = gson.toJson(userCreateForm);

        //when&then
        MvcResult result = mockMvc.perform(
                    post("/api/v1/user/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(form)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isOk())
                .andReturn();

        /* get result  */
        String response = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("response = " + response);

    }


}
