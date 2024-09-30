package school.faang.user_service.service;

import school.faang.user_service.dto.UserDto;

import java.io.InputStream;
import java.util.List;

public interface UserService {

    void deactivateUser(Long id);

    UserDto getUser(long id);

    List<UserDto> getUsersByIds(List<Long> ids);

    List<UserDto> uploadFile(InputStream inputStream);
}
