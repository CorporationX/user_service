package school.faang.user_service.filter.user;

import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.Objects;
import java.util.stream.Stream;

public class UserContactFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return Objects.nonNull(filters.getContactPattern());
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filters) {
        return users.filter(user -> user.getContacts().stream()
                .anyMatch(contact -> contact.getContact().contains(filters.getContactPattern())));
    }
}
