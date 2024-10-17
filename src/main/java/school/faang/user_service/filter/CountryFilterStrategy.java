package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.model.filter_dto.UserFilterDto;
import school.faang.user_service.model.entity.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CountryFilterStrategy implements UserFilterStrategy {

    @Override
    public boolean applyFilter(UserFilterDto filter) {
        return filter.getCountryPattern() != null && !filter.getCountryPattern().isEmpty();
    }

    @Override
    public List<User> filter(List<User> users, UserFilterDto filter) {
        return users.stream()
                .filter(user -> user.getCountry() != null &&
                        user.getCountry().getTitle().contains(filter.getCountryPattern()))
                .collect(Collectors.toList());
    }
}
