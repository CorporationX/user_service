package school.faang.user_service.config.context;

import org.springframework.stereotype.Component;

@Component
public class UserContext {
    private final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();
    private final ThreadLocal<String> userNameHolder = new ThreadLocal<>();

    public void setUserId(long userId) {
        userIdHolder.set(userId);
    }

    public long getUserId() {
        Long userId = userIdHolder.get();
        if (userId == null) {
            throw new IllegalArgumentException("User ID is missing. Please make sure 'x-user-id' header is included in the request.");
        }
        return userId;
    }

    public void setUserName(String userName) {
        userNameHolder.set(userName);
    }

    public String getUserName() {
        String userName = userNameHolder.get();
        if (userName == null) {
            throw new IllegalArgumentException("User ID is missing. Please make sure 'x-user-name' header is included in the request.");
        }
        return userName;
    }

    public void clear() {
        userIdHolder.remove();
        userNameHolder.remove();
    }
}
