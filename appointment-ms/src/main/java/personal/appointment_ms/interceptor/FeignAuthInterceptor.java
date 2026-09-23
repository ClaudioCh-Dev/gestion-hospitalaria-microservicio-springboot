package personal.appointment_ms.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Component
public class FeignAuthInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {

        RequestAttributes attributes =
                RequestContextHolder.getRequestAttributes();

        log.info("FeignAuthInterceptor - RequestAttributes={}", attributes);

        if (attributes == null) {
            log.warn("FeignAuthInterceptor - RequestAttributes NULL");
            return;
        }

        HttpServletRequest request =
                ((ServletRequestAttributes) attributes).getRequest();

        log.info("Feign X-User-Id={}", request.getHeader("X-User-Id"));
        log.info("Feign X-Role={}", request.getHeader("X-Role"));
        log.info("Feign X-Permissions={}", request.getHeader("X-Permissions"));

        copyHeader(request, template, "X-User-Id");
        copyHeader(request, template, "X-Role");
        copyHeader(request, template, "X-Permissions");
    }

    private void copyHeader(
            HttpServletRequest request,
            RequestTemplate template,
            String header) {

        String value = request.getHeader(header);

        if (value != null) {
            template.header(header, value);
        }
    }
}