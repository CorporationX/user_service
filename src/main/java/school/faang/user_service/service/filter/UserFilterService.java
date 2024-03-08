package school.faang.user_service.service.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.filter.user.UserFilter;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserFilterService {

    private final List<UserFilter> userFilters;

    public Stream<UserDto> applyFilters(Stream<UserDto> userDtoStream, UserFilterDto filterDto) {
        if (filterDto != null) {
            for (UserFilter userFilter : userFilters) {
                if (userFilter.isApplicable(filterDto)) {
                    userDtoStream = userFilter.apply(userDtoStream, filterDto);
                }
            }
        }
        return userDtoStream;
    }

}
