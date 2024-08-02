package school.faang.user_service.service.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;

@Component("EventUserService")
public class UserService {
    public User findUserById(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }
}
