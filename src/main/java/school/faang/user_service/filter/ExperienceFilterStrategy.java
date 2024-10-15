package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.model.filter_dto.UserFilterDto;
import school.faang.user_service.model.entity.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ExperienceFilterStrategy implements UserFilterStrategy {

    @Override
    public boolean applyFilter(UserFilterDto filter) {
        return filter.getExperienceMin() > 0 || filter.getExperienceMax() > 0;
    }

    @Override
    public List<User> filter(List<User> users, UserFilterDto filter) {
        return users.stream()
                .filter(user -> (filter.getExperienceMin() <= user.getExperience()
                        && user.getExperience() <= filter.getExperienceMax()))
                .collect(Collectors.toList());
    }
}
