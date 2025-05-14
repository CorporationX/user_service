package school.faang.user_service.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.regex.Pattern;

@Component
@Slf4j
public class FollowerPhonePatternFilter implements UserFollowersFilter {
    private static final String REGEX_NON_DIGIT_OR_PLUS = "[^\\d+]";
    private static final String REGEX_OPTIONAL_LEADING_PLUS = "^\\+?";
    private static final String REGEX_ANY_CHAR_SEQUENCE_TO_END = ".*$";
    private static final String REGEX_ONE_OR_MORE_LEADING_PLUSES = "^\\++";

    @Override
    public boolean isApplicable(User follower, UserFilterDto filter) {
        return filter.getPhonePattern() != null && !filter.getPhonePattern().isBlank();
    }

    @Override
    public boolean test(User follower, UserFilterDto filter) {
        if (follower.getPhone() == null) {
            return false;
        }
        String normalized = filter.getPhonePattern().replaceAll(REGEX_NON_DIGIT_OR_PLUS, "");
        String digitsOnly = normalized.replaceAll(REGEX_ONE_OR_MORE_LEADING_PLUSES, "");
        String regex = REGEX_OPTIONAL_LEADING_PLUS + Pattern.quote(digitsOnly) + REGEX_ANY_CHAR_SEQUENCE_TO_END;
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(follower.getPhone()).find();
    }
}
