package school.faang.user_service.filter.user;

import io.netty.util.internal.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class UserCountryFilter extends UserFilter {
    @Override
    public Object getFilterFieldValue(UserFilterDto filters) {
        return filters.getCountryPattern();
    }

    @Override
    public boolean apply(User user, UserFilterDto filters) {
        return Objects.requireNonNullElse(user.getCountry(), Country.builder().build())
                .getTitle().contains(filters.getCountryPattern());
    }
}
