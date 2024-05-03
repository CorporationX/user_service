package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserContactFilter implements UserFilter {

    @Override
    @Transactional
    public Stream<User> applyFilter(Stream<User> users, UserFilterDto userFilterDto) {
        if (userFilterDto.getContactPattern() != null) {
            return users.filter(user -> user.getContacts().stream()
                    .anyMatch(contact -> contact.getContact().startsWith(userFilterDto.getContactPattern())));
        }
        return users;
    }
}
