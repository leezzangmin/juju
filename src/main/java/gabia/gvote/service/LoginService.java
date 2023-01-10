package gabia.gvote.service;

import gabia.gvote.dto.IdPwDTO;
import gabia.gvote.entity.MemberAuth;
import gabia.gvote.repository.MemberAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.UUID.randomUUID;

@RequiredArgsConstructor
@Service
public class LoginService {

    private final static String COOKIE_NAME = "JUJUCOOKIEID";
    private final Map<String, Long> cookieMemberIds = new ConcurrentHashMap<>();

    private final MemberAuthRepository memberAuthRepository;

    @Transactional(readOnly = true)
    public Long login(IdPwDTO idPwDTO) {
        MemberAuth memberAuth = memberAuthRepository.findByStringId(idPwDTO.getMemberStringId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (!memberAuth.getMemberStringPw().equals(idPwDTO.getMemberPw())) {
            throw new IllegalArgumentException("ID 혹은 PW가 일치하지 않습니다.");
        }

        return memberAuth.getMember().getMemberId();
    }

    public void loginAfterProcess(Long memberId, HttpServletResponse response) {
        String UUID = randomUUID().toString();
        cookieMemberIds.put(UUID, memberId);

        Cookie cookie = new Cookie(COOKIE_NAME, UUID);
        response.addCookie(cookie);
    }


    public Long getMemberId(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new IllegalArgumentException("로그인하지 않은 사용자");
        }

        return Arrays.stream(cookies)
                .filter(c -> c.getName().equals(COOKIE_NAME))
                .findAny()
                .map(c -> cookieMemberIds.get(c.getValue()))
                .orElseThrow(() -> new IllegalArgumentException("인증 오류"));
    }

}
