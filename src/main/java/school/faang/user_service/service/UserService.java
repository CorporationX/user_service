package school.faang.user_service.service;

import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.model.dto.UserDto;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.model.filter_dto.user.UserFilterDto;

import java.io.InputStream;
import java.util.List;

public interface UserService {
    List<UserDto> getPremiumUsers(UserFilterDto filterDto);

    UserDto deactivateUser(UserDto userDto);

    User stopUserActivities(User user);

    User stopGoals(User user);

    User stopEvents(User user);

    UserDto getUser(long userId);

    List<UserDto> getUsersByIds(List<Long> ids);

    List<UserDto> getFilteredUsers(UserFilterDto filterDto, Long callingUserId);

    void saveAvatar(long userId, MultipartFile file);

    byte[] getAvatar(long userId);

    void deleteAvatar(long userId);

    void processCsvFile(InputStream inputStream);

    Long getMaxUserId();

    void updateTelegramUserId(String telegramUserName, String telegramUserId);

    void publishProfileViewEvent(long viewerId, long profileOwnerId);
}
