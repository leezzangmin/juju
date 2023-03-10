package gabia.gvote.argumentresolver;

import gabia.gvote.dto.SessionMemberAuthDTO;
import gabia.gvote.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@Component
public class AuthArgumentResolver implements HandlerMethodArgumentResolver {

    private final LoginService loginService;
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasAuthAnnotation = parameter.hasParameterAnnotation(Auth.class);
        boolean hasSessionMemberAuthDTO = SessionMemberAuthDTO.class.isAssignableFrom(parameter.getParameterType());

        return hasAuthAnnotation && hasSessionMemberAuthDTO;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        SessionMemberAuthDTO sessionMemberAuthDTO = loginService.extractMemberAuthResourceFromCookie(request);
        return sessionMemberAuthDTO;
    }
}
