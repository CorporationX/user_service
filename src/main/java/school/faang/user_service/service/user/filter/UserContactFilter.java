package school.faang.user_service.service.user.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;

import java.util.List;
import java.util.stream.Stream;

@Component
class UserContactFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.getContactPattern() != null && !filters.getContactPattern().isBlank();
    }

    @Override
    public Stream<User> apply(List<User> users, UserFilterDto filters) {
        return users.stream()
                .filter(user -> {
                    var matchedContactsList = user.getContacts().stream()
                            .map(Contact::getContact)
                            .filter(contact -> contact.matches(filters.getContactPattern()))
                            .toList();

                    return matchedContactsList.size() > 0;
                });
    }
}
