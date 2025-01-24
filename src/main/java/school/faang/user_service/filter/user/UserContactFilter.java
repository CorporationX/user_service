package school.faang.user_service.filter.user;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;

import java.util.Collections;
import java.util.Objects;
import java.util.stream.Stream;

@Component
public class UserContactFilter extends UserFilter {
    @Override
    public Object getFilterFieldValue(UserFilterDto filters) {
        return filters.getContactPattern();
    }

    @Override
    public boolean apply(User user, UserFilterDto filters) {
        return Objects.requireNonNullElse(user.getContacts(), Collections.<Contact>emptyList())
                .stream()
                .anyMatch(contact ->
                        contact.getContact()
                                .contains(filters.getContactPattern()));
    }
}
