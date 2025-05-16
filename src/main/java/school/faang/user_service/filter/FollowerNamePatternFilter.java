package school.faang.user_service.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.text.Normalizer;
import java.util.regex.Pattern;

@Component
@Slf4j
public class FollowerNamePatternFilter implements UserFollowersFilter {
    @Override
    public boolean isApplicable(User follower, UserFilterDto filter) {
        return filter.getNamePattern() != null && !filter.getNamePattern().isBlank();
    }

    @Override
    public boolean test(User follower, UserFilterDto filter) {
        String regex = "\\b" + Pattern.quote(normalize(filter.getNamePattern()));
        Pattern pattern = Pattern.compile(regex,
                Pattern.CASE_INSENSITIVE
                        | Pattern.UNICODE_CASE
                        | Pattern.UNICODE_CHARACTER_CLASS);
        return pattern.matcher(normalize(follower.getUsername())).find();
    }

    private String normalize(String input) {
        String nfd = Normalizer.normalize(input, Normalizer.Form.NFD);
        String noDiacritics = nfd.replaceAll("\\p{InCOMBINING_DIACRITICAL_MARKS}+", "");
        return noDiacritics.trim().replaceAll("\\s{2,}", " ").toLowerCase();
    }
}
