package school.faang.user_service.service.userban;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.service.UserService;

@Service
@RequiredArgsConstructor
public class UserBanEventWorker {
    private final UserService userService;

    public void saveUserBanEvent(String message) {
        userService.userBanEventSave(message);
    }
}
