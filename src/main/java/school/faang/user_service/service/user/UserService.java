package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.lang.reflect.Field;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> getPremiumUsers(UserFilterDto filter) {
        return userRepository.findPremiumUsers()
                .filter(user -> applyFilter(user, filter))
                .map(userMapper::toDto)
                .toList();
    }

    private boolean applyFilter(User user, UserFilterDto filter) {
        return checkStringFieldsMatch(user, filter);
    }

    private boolean checkStringFieldsMatch(User user, UserFilterDto filter) {
        for (Field field : filter.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object filterValue = field.get(filter);
                if (filterValue instanceof String && StringUtils.isNotEmpty((String) filterValue)) {
                    Field userField = user.getClass().getDeclaredField(getUserFieldName(field.getName()));
                    userField.setAccessible(true);
                    String userValue = (String) userField.get(user);

                    if (!StringUtils.equals(userValue, (String) filterValue)) {
                        return false;
                    }
                }
                if (filterValue instanceof Integer) {
                    if (user.getExperience() < filter.getExperienceMin() || user.getExperience() > filter.getExperienceMax()) {
                        return false;
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                log.error(e.getMessage());
                return false;
            }
        }

        return true;
    }

    private String getUserFieldName(String filterFieldName) {
        return switch (filterFieldName) {
            case "namePattern" -> "username";
            case "aboutPattern" -> "aboutMe";
            case "emailPattern" -> "email";
            case "contactPattern" -> "contacts";
            case "cityPattern" -> "city";
            case "phonePattern" -> "phone";
            case "skillPattern" -> "skills";
            case "experienceMin", "experienceMax" -> "experience";
            default -> filterFieldName;
        };
    }
}
