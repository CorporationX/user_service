package school.faang.user_service.filter.subfilter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;
@Component
public class NamePatternFilter implements SubscriberFilter {

    @Override
    public boolean isApplicable(UserFilterDto filter) {
        return (filter.getNamePattern() != null && !filter.getNamePattern().isBlank());
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filterDto) {
        return users.filter(
                user -> user.getUsername().toLowerCase().contains(filterDto.getNamePattern().toLowerCase())
        );
    }
}

