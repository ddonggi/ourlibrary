package com.example.ourlibrary.books;

import com.example.ourlibrary.user.SiteUser;
import com.example.ourlibrary.user.SiteUserRepository;
import com.example.ourlibrary.user.SiteUserService;
import com.example.ourlibrary.user.UserCreateForm;
import com.google.gson.Gson;
import jakarta.persistence.EntityListeners;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * PackageName : com.example.ourlibrary.books
 * FileName : BookControllerTest
 * Author : dglee
 * Create : 3/2/24 11:29 PM
 * Description :
 **/

@WebMvcTest(BookController.class)//테스트할 특정 컨트롤러명
@WithMockUser(username = "test@test.com")
//@AutoConfigureMockMvc//컨트롤러뿐만 아니라 테스트 대상이 아닌 @Service나 @Repository가 붙은 객체들도 모두 메모리에 올린다
//@EntityListeners(AuditingEntityListener.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    //@WebMvcTest 어노테이션은 Web 레이어 관련 Bean들만 등록하기 때문에 Service가 등록되지 않기에 MockBean으로 가짜 객체를 등록해준다.
    @MockBean
    private BookService bookService;
    @MockBean
    private SiteUserService siteUserService;
    @MockBean
    private SiteUserRepository siteUserRepository;
    @MockBean
    private BookRepository bookRepository;


    @Test
    @DisplayName("도서 위탁 POST 테스트")
    public void bookConsignmentTest() throws Exception{
        //given
        int price = 3500;
        long ISBN = 1234567892123L;
        String bookName = "냠냠";
        BookForm bookForm = new BookForm();
        bookForm.setBookName(bookName);
        bookForm.setISBN(ISBN);
        bookForm.setRentPrice(price);

        Gson gson = new Gson();
        String form = gson.toJson(bookForm);

        //when&then
        MvcResult result = mockMvc.perform(
                        post("/api/v1/book/consignment")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(form)
                                .characterEncoding("UTF-8")
                )
//                .andExpect(content().json(form))
                .andExpect(status().isOk())
                .andReturn();

        /* get result  */
        String responseResult = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("responseResult = " + responseResult);
    }

    @Test
    @DisplayName("도서 대여 POST 테스트")
    public void bookRentalTest() throws Exception{
        //given
        List<Integer> bookIdList = new ArrayList<>();
        bookIdList.add(1);
        bookIdList.add(2);

        Gson gson = new Gson();
        String content = gson.toJson(bookIdList);

        //when&then
        MvcResult result = mockMvc.perform(
                        post("/api/v1/book/rental")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                                .characterEncoding("UTF-8")
                )
//                .andExpect(content().json(form))
                .andExpect(status().isOk())
                .andReturn();

        /* get result  */
        String responseResult = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("responseResult = " + responseResult);
    }
}

