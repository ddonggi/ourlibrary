package com.example.ourlibrary;

import com.example.ourlibrary.books.BookForm;
import com.example.ourlibrary.books.BookResponseDTO;
import com.example.ourlibrary.books.BookService;
import com.example.ourlibrary.user.SiteUserService;
import com.example.ourlibrary.user.UserCreateForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

/**
 * PackageName : com.example.ourlibrary
 * FileName : SwaggerController
 * Author : dglee
 * Create : 2/29/24 7:34 PM
 * Description : [VIEW] Thymeleaf 화면 만을 출력하는 컨트롤러
 **/
@Tag(name = "Template", description = "Thymeleaf 템플릿 View를 반환")
@RequestMapping(value = "/")
@Controller
@RequiredArgsConstructor
@Slf4j
public class TemplateController {

    private final SiteUserService siteUserService;
    private final BookService bookService;
    @GetMapping(value = "/")
    @Operation(summary = "메인 페이지", description = "메인페이지 화면을 출력합니다.")
    public String indexPage(Model model, Principal principal) {
        if(principal!=null) {
            log.info("login email:{}",principal.getName());
            model.addAttribute("siteUser", siteUserService.findByEmail(principal.getName()));
        }
        return "index";
    }
    @GetMapping(value = "/book/consignment")
    @Operation(summary = "책 위탁 페이지", description = "책 위탁 화면을 출력합니다.")
    @Parameter(name = "model", description = "템플릿 모델")
    public String bookConsignmentView(Model model, Principal principal) {
        if(principal!=null)
            model.addAttribute("siteUser", siteUserService.findByEmail(principal.getName()));
        return "books/consignment";
    }

    @GetMapping(value = "/book/rental")
    @Operation(summary = "책 대여 페이지", description = "책 대여 화면을 출력합니다.")
    @Parameter(name = "model", description = "템플릿 모델")
    public String bookRentalView(Model model, Principal principal) {
        Page<BookResponseDTO> paging = bookService.getBookPage(0,0);
        model.addAttribute("paging", paging);
        if(principal!=null)
            model.addAttribute("siteUser", siteUserService.findByEmail(principal.getName()));
        return "books/rental";
    }

    @GetMapping(value = "/user/signup")
    @Operation(summary = "회원가입 페이지", description = "회원가입 화면을 출력합니다.")
    @Parameter(name = "model", description = "템플릿 모델")
    public String userSignupView(Model model) {
        model.addAttribute("title", "회원가입 페이지");
        return "user/signup";
    }

    @GetMapping(value = "/user/login")
    @Operation(summary = "로그인 페이지", description = "로그인 화면을 출력합니다.")
    public String userLoginView() {
        return "user/login";
    }
}
