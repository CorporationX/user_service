package school.faang.user_service.service.user;

import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;

import java.util.List;

@Service
public interface UserService {

    List<UserDto> getPremiumUsers(UserFilterDto userFilterDto);
}
