package school.faang.user_service.service.user;

import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto getUser(long userId);

    List<UserDto> getUsersByIds(List<Long> userIds);

    void uploadUsers(MultipartFile file);
}