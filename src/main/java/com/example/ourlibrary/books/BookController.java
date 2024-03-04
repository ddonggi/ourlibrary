package com.example.ourlibrary.books;

import com.example.ourlibrary.user.SiteUser;
import com.example.ourlibrary.user.SiteUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * PackageName : com.example.ourlibrary.books
 * FileName : BookController
 * Author : dglee
 * Create : 2/29/24 8:23 PM
 * Description :
 **/

@Tag(name = "BookController", description = "Book과 관련된 요청")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/book")
@Slf4j
public class BookController {
    private final BookRepository bookRepository;

    private final BookService bookService;
    private final SiteUserService siteUserService;

    @GetMapping("/list")
    @Operation(summary = "책 페이징", description = "책 페이징 요청")
    @Parameter(name = "page", description = "페이지 넘버")
    @Parameter(name = "category", description = "카테고리 번호")
    public ResponseEntity<Object> bookConsignment(
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "category",defaultValue = "0") int category
    ){
        log.info("page:{},category:{}",page,category);
        Page<BookResponseDTO> paging = bookService.getBookPage(page,category);
        return ResponseEntity.status(HttpStatus.OK).body(paging);
    }

    @PostMapping("/consignment")
    @Operation(summary = "책 위탁", description = "책 위탁 요청")
    @Parameter(name = "bookForm", description = "폼을 통해 들어온 책 정보")
    public ResponseEntity<Object> bookConsignment(
            @Valid @RequestBody BookForm bookForm,
            BindingResult bindingResult,
            Principal principal
    ) {
        log.info("principal:{}", principal);
        log.info("bindingResult:{}", bindingResult);

        Map<String, Object> body = new HashMap<>();
        if(bindingResult.hasErrors()){
            body.put("errors", Objects.requireNonNull(bindingResult.getFieldErrors()));
            body.put("message", "양식을 지켜주세요");
            return ResponseEntity.ok().body(body);
        }
        if(principal!=null) {
            log.info("principal name:{}", principal.getName());
            SiteUser siteUser = siteUserService.findByEmail(principal.getName());
            log.info("siteUser:{}", siteUser);
            return getBookResponseEntity(bookForm, body, siteUser);
        }else{
            body.put("message", "잘못된 접근입니다. 로그인 후 이용해 주세요");
            return ResponseEntity.badRequest().body(body);
        }
    }

    @PostMapping("/rental")
    @Operation(summary = "책 대여", description = "책 대여 요청")
    @Parameter(name = "bookIdList", description = "책 id 리스트")
    public ResponseEntity<Object> bookRental(
            @RequestBody List<Long> bookIdList,
            Principal principal
    ) {
        log.info("책 대여 요청");
        log.info("bookIdList:{}",bookIdList);
        List<Book> bookList = new ArrayList<>();
        for(long id: bookIdList){
            if(bookRepository.findById(id).isPresent())
                bookList.add(bookRepository.findById(id).get());
        }
        Map<String,String> body = new HashMap<>();

        if(principal!=null) {
            SiteUser borrower = siteUserService.findByEmail(principal.getName());
            if(bookList.size()<1){
                body.put("message", "찾는 도서가 없습니다");
                return ResponseEntity.ok().body(body);
            }
            String message = bookService.rentalBooks(bookList,borrower);
            body.put("message", message);
            // 10~20초 뒤에 반납 실행
            CompletableFuture.delayedExecutor((10 + (int) (Math.random() * 11)), TimeUnit.SECONDS)
                    .execute(() -> bookService.returnBooks(bookList,borrower));
            return ResponseEntity.ok().body(body);
        }else{
            body.put("message", "잘못된 접근입니다. 로그인 후 이용해 주세요");
            return ResponseEntity.badRequest().body(body);
        }
    }

    @PostMapping("/return")
    @Operation(summary = "책 반납", description = "책 반납 요청")
    public ResponseEntity<Object> bookReturn(){
        Map<String,String> signUp = new HashMap<>();
        signUp.put("message", "책 반납 완료");
        return ResponseEntity.ok().body(signUp);
    }


    @NotNull
    private ResponseEntity<Object> getBookResponseEntity(@RequestBody @Valid BookForm bookForm, Map<String, Object> body, SiteUser siteUser) {
        Optional<Book> optionalBook = Optional.ofNullable(bookService.consignment(bookForm, siteUser));
        if (optionalBook.isEmpty()) {
            body.put("message", "등록이 되지 않았습니다");
            return ResponseEntity.ok().body(body);
        }
        Book book = optionalBook.get();
        log.info("bookId:{}", book.getId());
        body.put("success", "책 위탁 완료");
        body.put("book", book);
        return ResponseEntity.ok().body(body);
    }
}
