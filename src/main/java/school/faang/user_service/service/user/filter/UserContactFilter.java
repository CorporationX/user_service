package school.faang.user_service.service.user.filter;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

public class UserContactFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.getContactPattern() != null;
    }

    @Override
    public Stream<User> apply(List<User> users, UserFilterDto filters) {
        return users.stream().filter(user -> user.getContacts().stream().anyMatch(contact -> contact.getContact().contains(filters.getContactPattern())));
    }
}
