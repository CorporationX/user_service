package school.faang.user_service.service;

import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;

import java.util.List;

@Service
public interface UserService {

    List<UserDto> getPremiumUsers(UserFilterDto userFilterDto);
}
