package school.faang.user_service.filter.userFilter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

@Component
public class UserContactFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto userFilterDto) {
        return userFilterDto.getContactPattern() != null;
    }

    @Override
    public Stream<User> apply(List<User> users, UserFilterDto userFilter) {
        return users.stream().filter(user -> user.getContacts().stream()
                .anyMatch(contact -> contact.getContact().contains(userFilter.getContactPattern())));
    }
}
