package com.example.ourlibrary.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * PackageName : com.example.ourlibrary.SiteUser
 * FileName : SiteUserController
 * Author : dglee
 * Create : 2/29/24 4:23 PM
 * Description :
 **/


@Tag(name = "UserController", description = "User와 관련된 요청")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/user")
public class SiteUserController {

    private final SiteUserService siteUserService;

    @PostMapping(value = "/signup")
    @Operation(summary = "회원가입", description = "회원가입을 요청합니다")
    @Parameter(name = "userCreateForm", description = "폼을 통해 들어온 유저 정보")
    public ResponseEntity<Object> signUp(
            @Valid @RequestBody UserCreateForm userCreateForm,
            BindingResult bindingResult
    ) {
        log.info("userCreateform:{}",userCreateForm);
        Map<String,Object> body = new HashMap<>();
        Map<String,String> messageMap = new HashMap<>();

        if (bindingResult.hasErrors()) {
            log.info("에러발생:{}",bindingResult.getFieldError());
            messageMap.put("error", String.valueOf(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage()));
            body.put("message",messageMap);
            return ResponseEntity.ok().body(body);
        }
        if(siteUserService.emailExist(userCreateForm.getEmail())){
            log.info("이미 사용중인 이메일 이에요");
            messageMap.put("email", "이미 사용중인 이메일 입니다.");
            body.put("message",messageMap);
            return ResponseEntity.ok().body(body);
        }
        try {
            SiteUser siteUser = siteUserService.create(userCreateForm);
            messageMap.put("success", "회원가입이 완료되었습니다 :)");
            body.put("siteUser", siteUser);
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            messageMap.put("error", "이미 등록된 사용자 입니다");
        } catch (Exception e) {// bindingResult.reject(오류코드, 오류메시지)는 특정 필드의 오류가 아닌 일반적인 오류를 등록할때 사용한다.
            e.printStackTrace();
            messageMap.put("error", e.getMessage());
        }
        body.put("message",messageMap);
        return ResponseEntity.ok().body(body);
    }

}

