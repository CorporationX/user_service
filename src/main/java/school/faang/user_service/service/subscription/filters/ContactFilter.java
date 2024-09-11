package school.faang.user_service.service.subscription.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.subscription.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class ContactFilter implements UserFilter {

    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.contactPattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filters) {
        return users
                .filter(user -> user.getContacts() != null && user.getContacts().stream()
                        .anyMatch(contact -> contact.getContact() != null &&
                                contact.getContact().contains(filters.contactPattern())));
    }
}
