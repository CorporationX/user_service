package school.faang.user_service.filter.subfilter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;
@Component
public class EmailPatternFilter implements SubscriberFilter {
    @Override
    public boolean isApplicable(UserFilterDto filter) {
        return filter.getEmailPattern() != null && !filter.getEmailPattern().isBlank();
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filterDto) {
        return users.filter(
                user -> user.getEmail().toLowerCase().contains(filterDto.getEmailPattern().toLowerCase().trim())
        );
    }
}
