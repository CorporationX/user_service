package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.model.dto.UserDto;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.model.filter_dto.user.UserFilterDto;

import java.io.InputStream;
import java.util.List;

public interface UserService {
    @Transactional
    List<UserDto> getPremiumUsers(UserFilterDto filterDto);

    UserDto deactivateUser(UserDto userDto);

    User stopUserActivities(User user);

    User stopGoals(User user);

    User stopEvents(User user);

    UserDto getUser(long userId);

    List<UserDto> getUsersByIds(List<Long> ids);

    @Transactional
    List<UserDto> getFilteredUsers(UserFilterDto filterDto, Long callingUserId);

    @Transactional
    void saveAvatar(long userId, MultipartFile file);

    byte[] getAvatar(long userId);

    @Transactional
    void deleteAvatar(long userId);

    @Transactional
    void processCsvFile(InputStream inputStream);
}
