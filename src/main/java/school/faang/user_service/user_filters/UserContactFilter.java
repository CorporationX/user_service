package school.faang.user_service.user_filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Component
public class UserContactFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.getContactPattern() != null && !filters.getContactPattern().isBlank();
    }

    @Override
    public void apply(List<User> users, UserFilterDto filters) {
        users.removeIf(user -> user.getContacts().stream()
                .noneMatch(contact -> contact.getContact().contains(filters.getContactPattern())));
    }
}