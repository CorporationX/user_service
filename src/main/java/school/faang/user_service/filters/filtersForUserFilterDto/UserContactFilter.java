package school.faang.user_service.filters.filtersForUserFilterDto;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.filters.UserFilterDto;

import java.util.stream.Stream;

@Component
public class UserContactFilter implements DtoUserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.getContactPattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filterDto) {
        return users.filter(user -> user.getContacts().stream().anyMatch(contact ->
                contact.getContact().contains(filterDto.getContactPattern())));
    }
}
