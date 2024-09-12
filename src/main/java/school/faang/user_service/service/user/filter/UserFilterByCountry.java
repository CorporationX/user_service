package school.faang.user_service.service.user.filter;

import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.Objects;

public class UserFilterByCountry implements UserFilter{
    @Override
    public boolean isApplicable(UserFilterDto filterDto) {
        return Objects.nonNull(filterDto.getCountry());
    }

    @Override
    public boolean test(User user, UserFilterDto filterDto) {
        String countryName =  user.getCountry().getTitle();
        return filterDto.getCountry().contains(countryName);
    }
}
