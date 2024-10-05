package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.exception.user.UserContextException;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserContextService {
    private final UserContext userContext;

    public long getContextUserId() {
        try {
            return userContext.getUserId();
        } catch (IllegalArgumentException exception) {
            throw new UserContextException(exception.getMessage());
        }
    }
}
