package school.faang.user_service.service.user;

import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.image.AvatarSize;

import java.awt.image.BufferedImage;
import java.util.List;

public interface UserService {
    UserDto getUser(long userId);

    List<UserDto> getUsersByIds(List<Long> userIds);

    UserDto uploadUserAvatar(Long userId, BufferedImage uploadedImage);

    byte[] downloadUserAvatar(long userId, AvatarSize size);

    void deleteUserAvatar(long userId);
}