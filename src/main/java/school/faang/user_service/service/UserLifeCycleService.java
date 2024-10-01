package school.faang.user_service.service;

import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserRegistrationDto;

import java.util.List;

public interface UserLifeCycleService {

    void deactivateUser(Long id);

    UserDto registrationUser(UserRegistrationDto userRegistrationDto);

    void banUsersById(List<Long> ids);
}
