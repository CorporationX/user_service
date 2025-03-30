package school.faang.user_service.service.user;

import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.promotion.PromoUserDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserResponseDto;
import school.faang.user_service.entity.User;

import java.util.List;

public interface UserService {
    void processPersonsFromFile(MultipartFile file);

    User findByIdOrThrow(long userId);

    UserResponseDto getUser(Long userId);

    List<PromoUserDto> getUsersByIds(List<Long> ids);
}
