package personal.billing_ms.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;


@Component
public class FeignAuthInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {

        RequestAttributes attributes =
                RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return;
        }

        HttpServletRequest request =
                ((ServletRequestAttributes) attributes).getRequest();

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