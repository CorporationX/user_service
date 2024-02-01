package school.faang.user_service.service.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.entity.UserDto;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.filter.memory.user.UserInMemoryFilter;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserInMemoryFilterService implements InMemoryFilterService<UserDto, UserFilterDto> {

    private final List<UserInMemoryFilter> userFilters;

    @Override
    public Stream<UserDto> applyFilters(Stream<UserDto> userDtoStream, UserFilterDto filterDto) {
        if (filterDto != null) {
            for (UserInMemoryFilter userFilter : userFilters) {
                if (userFilter.isApplicable(filterDto)) {
                    userDtoStream = userFilter.apply(userDtoStream, filterDto);
                }
            }
        }
        return userDtoStream;
    }

}
