package personal.doctor_ms.security;

import org.springframework.stereotype.Component;

@Component("auth")
public class AuthorizationService {

    public boolean hasPermission(String permission) {

        UserContext context = UserContextHolder.get();

        return context != null &&
               context.hasPermission(permission);
    }
}