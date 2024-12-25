package school.faang.user_service.filter.userFilter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.model.User;
import school.faang.user_service.model.contact.Contact;

@Component
public class ContactPatternFilter extends UserFilter {

    @Override
    public boolean isApplicable(UserFilterDto filters) {
        this.pattern = filters.contactPattern();
        return pattern != null;
    }

    @Override
    public boolean apply(User user) {
        return user.getContacts() != null && user.getContacts().stream()
                .map(Contact::getContact)
                .anyMatch(contact -> contact.contains(pattern));
    }
}
