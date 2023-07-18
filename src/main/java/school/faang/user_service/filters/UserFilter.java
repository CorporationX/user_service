package school.faang.user_service.filters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.commonMessages.ErrorMessages;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filters.filtersForUserFilterDto.DtoUserFilter;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserFilter {
    private final List<DtoUserFilter> userFilters;
    private final UserMapper userMapper;

    public List<UserDto> applyFilter(List<User> users, UserFilterDto DtoFilters){
        validateUsers(users);
        userFilters.stream()
                .filter(filter -> filter.isApplicable(DtoFilters))
                .forEach(filter -> filter.apply(users.stream(), DtoFilters));
        return userMapper.toUserDtoList(users);
    }

    private void validateUsers(List<User> users){
        for (User user : users) {
            if (user == null) {
                throw new NullPointerException(ErrorMessages.USER_IS_NULL);
            }
        }
    }
}
