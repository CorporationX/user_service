package school.faang.user_service.service.user.filter;

import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;

import java.util.stream.Stream;

public class UserContactFilter implements UserFilter{
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.getContactPattern() != null  && !filters.getContactPattern().isBlank();
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filters) {
        return users.filter(user -> {
            var matchedContactsList = user.getContacts().stream()
                    .map(Contact::getContact)
                    .filter(contact -> contact.matches(filters.getContactPattern()))
                    .toList();

            return matchedContactsList.size() > 0;
        });
    }
}
