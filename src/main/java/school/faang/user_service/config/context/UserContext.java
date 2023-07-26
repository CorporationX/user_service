package school.faang.user_service.config.context;

import org.springframework.stereotype.Component;

@Component
public class UserContext {

    private final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();

    public long getUserId() {
        return userIdHolder.get();
    }

    public void setUserId(long userId) {
        userIdHolder.set(userId);
    }

    public void clear() {
        userIdHolder.remove();
    }
}
