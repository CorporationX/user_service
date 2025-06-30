package school.faang.user_service.service;

import school.faang.user_service.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto getUserById(Long id);

    List<UserDto> getUsersByIds(List<Long> ids);

    void notifyGoalCompleted(Long userId, Long goalId);
}

