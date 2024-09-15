package school.faang.user_service.dto.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class ContactFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filter) {
        return filter.getContactPattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filter) {
        return users.filter(user -> user.getContacts().stream().anyMatch(contact -> contact.getContact().contains(filter.getContactPattern())));
    }
}
