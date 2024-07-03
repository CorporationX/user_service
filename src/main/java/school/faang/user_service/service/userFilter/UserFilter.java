package school.faang.user_service.service.userFilter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public interface UserFilter {
    boolean isApplicable(UserFilterDto userFilterDto);

    void apply(Stream<User> users, UserFilterDto userFilter);
}
