package school.faang.user_service.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

@Component
public interface UserFilter {
    boolean isApplicable(UserFilterDto filters);

    void apply(List<User> users, UserFilterDto filterDto);
}
