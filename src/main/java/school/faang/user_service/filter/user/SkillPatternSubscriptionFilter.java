package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.userSubscriptionDto.UserSubscriptionFilterDto;
import school.faang.user_service.entity.User;

import java.util.regex.Pattern;
import java.util.stream.Stream;

@Component
public class SkillPatternSubscriptionFilter implements UserSubscriptionFilter {
    @Override
    public boolean isApplication(UserSubscriptionFilterDto userSubscriptionFilterDto) {
        return userSubscriptionFilterDto.getSkillPattern() != null && !userSubscriptionFilterDto.getSkillPattern().isEmpty();
    }

    @Override
    public Stream<User> apply(Stream<User> userStream, UserSubscriptionFilterDto userSubscriptionFilterDto) {
        String skillPattern = userSubscriptionFilterDto.getSkillPattern();
        Pattern pattern = Pattern.compile(skillPattern);
        return userStream.filter(user -> user.getSkills().stream()
                .anyMatch(skill -> pattern.matcher(skill.getTitle()).find()));
    }

}
