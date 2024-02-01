package school.faang.user_service.filter.memory.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.entity.UserDto;
import school.faang.user_service.dto.filter.UserFilterDto;

import java.util.stream.Stream;

@Component
public class IdUserInMemoryFilter implements UserInMemoryFilter {

    @Override
    public boolean isApplicable(UserFilterDto userFilterDto) {
        return userFilterDto.getId() != null && userFilterDto.getId() > 0;
    }

    @Override
    public Stream<UserDto> apply(Stream<UserDto> stream, UserFilterDto filterDto) {
        return stream.filter(userDto -> userDto.getId().equals(filterDto.getId()));
    }

}
