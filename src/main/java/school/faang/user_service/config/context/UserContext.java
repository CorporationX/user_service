package school.faang.user_service.config.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.UnauthorizedException;

@Slf4j
@Component
public class UserContext {

    private final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();

    public void setUserId(long userId) {
        userIdHolder.set(userId);
    }

    public long getUserId() {
        Long userId = userIdHolder.get();
        log.debug("into UserContext.getUserId userId: [{}]", userId);
        if (userId == null) {
            throw new UnauthorizedException("User ID is missing. Please make sure 'x-user-id' " +
                    "header is included in the request.");
        }
        return userId;
    }

    public void clear() {
        userIdHolder.remove();
    }
}
