package school.faang.user_service.filter.subFilter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;
@Component
public class ContactPatternFilter implements SubscriberFilter {
    @Override
    public boolean isApplicable(UserFilterDto filter) {
        return filter.getContactPattern() != null && !filter.getContactPattern().isBlank();
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filterDto) {
        return users.filter(
                user -> user.getContacts()
                        .stream()
                        .anyMatch(contact -> contact.getContact()
                                .toLowerCase()
                                .contains(filterDto.getContactPattern().toLowerCase()))
        );
    }
}
