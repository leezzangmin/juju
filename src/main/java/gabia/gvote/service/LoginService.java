package gabia.gvote.service;

import gabia.gvote.dto.IdPwDTO;
import gabia.gvote.dto.SessionMemberAuthDTO;
import gabia.gvote.entity.MemberAuth;
import gabia.gvote.entity.MemberGubun;
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

    private final static String SESSION_ID = "JUJU_SESSION_ID";
    private final Map<String, SessionMemberAuthDTO> normalMemberSession = new ConcurrentHashMap<>();
    private final Map<String, SessionMemberAuthDTO> adminMemberSession =  new ConcurrentHashMap<>();

    private final MemberAuthRepository memberAuthRepository;

    @Transactional(readOnly = true)
    public SessionMemberAuthDTO login(IdPwDTO idPwDTO) {
        MemberAuth memberAuth = memberAuthRepository.findByStringId(idPwDTO.getMemberStringId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (!memberAuth.getMemberStringPw().equals(idPwDTO.getMemberPw())) {
            throw new IllegalArgumentException("ID 혹은 PW가 일치하지 않습니다.");
        }

        return new SessionMemberAuthDTO(memberAuth.getMember().getMemberId(), memberAuth.getMemberGubun());
    }

    public void loginAfterProcess(SessionMemberAuthDTO sessionMemberAuthDTO, HttpServletResponse response) {
        String UUID = randomUUID().toString();
        Cookie cookie = new Cookie(SESSION_ID, UUID);
        response.addCookie(cookie);

        if (sessionMemberAuthDTO.getMemberGubun() == MemberGubun.ADMIN) {
            adminMemberSession.put(UUID, sessionMemberAuthDTO);
            return;
        }
        normalMemberSession.put(UUID, sessionMemberAuthDTO);
    }


    public SessionMemberAuthDTO extractMemberAuthResourceFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new IllegalArgumentException("로그인하지 않은 사용자");
        }

        Cookie cookie = Arrays.stream(cookies)
                .filter(c -> c.getName().equals(SESSION_ID))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("인증 오류"));

        if (normalMemberSession.containsKey(cookie.getValue())) {
            return normalMemberSession.get(cookie.getValue());
        }
        if (adminMemberSession.containsKey(cookie.getValue())) {
            return adminMemberSession.get(cookie.getValue());
        }

        throw new IllegalArgumentException("인증 오류");
    }

    public static void main(String[] args) {
        Map<String, SessionMemberAuthDTO> test =  new ConcurrentHashMap<>();
        SessionMemberAuthDTO sessionMemberAuthDTO = test.get("asdf");
        System.out.println("sessionMemberAuthDTO = " + sessionMemberAuthDTO);
    }
}
