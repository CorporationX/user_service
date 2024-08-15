package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.userSubscriptionDto.UserSubscriptionFilterDto;
import school.faang.user_service.entity.User;

import java.util.regex.Pattern;
import java.util.stream.Stream;

@Component
public class ContactPatternSubscriptionFilter implements UserSubscriptionFilter {
    @Override
    public boolean isApplication(UserSubscriptionFilterDto userSubscriptionFilterDto) {
        return userSubscriptionFilterDto.getContactPattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> userStream, UserSubscriptionFilterDto userSubscriptionFilterDto) {
        String contactPattern = userSubscriptionFilterDto.getContactPattern();
        Pattern pattern = Pattern.compile(contactPattern);
        return userStream.filter(user -> user.getContacts().stream()
                .anyMatch(contact -> pattern.matcher(contact.getContact()).find()));
    }
}
