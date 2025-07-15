package school.faang.user_service.config.context;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.user.User;

@Component
@RequiredArgsConstructor
public class AuthUserContext {
    public User getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AuthenticationCredentialsNotFoundException("User is not authenticated");
        }
        Object principal = auth.getPrincipal();
        if (!(principal instanceof User user)) {
            throw new AuthenticationServiceException(
                    "Expected principal of type User but found " + principal.getClass().getName()
            );
        }
        return user;
    }

    public long getUserId() {
        return getUser().getId();
    }
}
