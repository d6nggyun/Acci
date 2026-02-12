package refresh.acci.global.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import refresh.acci.domain.user.model.CustomOAuthUser;
import refresh.acci.domain.user.model.CustomUserDetails;

@Slf4j
@Aspect
@Component
public class LoggingConfig {

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void loggingPointcut() {}

    @Around("loggingPointcut()")
    public Object logApiRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String userId = getCurrentUserId();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        long start = System.currentTimeMillis();
        String clientIp = getClientIp(request);

        try {
            return joinPoint.proceed();
        } finally {
            long duration = System.currentTimeMillis() - start;
            log.info("user={} ip={} {} {} {}ms", userId, clientIp, method, uri, duration);
        }
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return "anonymous";
        }

        Object principal = authentication.getPrincipal();

        //Spring 익명 사용자
        if (principal instanceof String) {
            return "anonymous";
        }

        //JWT 인증 (CustomUserDetails)
        if (principal instanceof CustomUserDetails userDetails) {
            return String.valueOf(userDetails.getId());
        }

        //OAuth 인증 (CustomOAuthUser)
        if (principal instanceof CustomOAuthUser oauthUser) {
            return String.valueOf(oauthUser.getId());
        }

        log.warn("알 수 없는 Principal 타입: {}", principal.getClass().getName());
        return "unknown";

    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        //여러 IP가 쉼표로 구분되어 있으면 첫 번째만
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}