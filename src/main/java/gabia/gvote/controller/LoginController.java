package gabia.gvote.controller;

import gabia.gvote.dto.IdPwDTO;
import gabia.gvote.dto.SessionMemberAuthDTO;
import gabia.gvote.service.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
public class LoginController {

    private final static String LOGIN_SUCCESS_MSG = "LOGIN SUCCESS";

    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<String> requestLogin(@Valid @RequestBody IdPwDTO idPwDTO, HttpServletResponse response) {
        SessionMemberAuthDTO sessionMemberAuthDTO = loginService.login(idPwDTO);
        loginService.loginAfterProcess(sessionMemberAuthDTO, response);

        return ResponseEntity.ok(LOGIN_SUCCESS_MSG);
    }


}
