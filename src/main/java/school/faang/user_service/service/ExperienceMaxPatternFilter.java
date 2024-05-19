package school.faang.user_service.service;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

public class ExperienceMaxPatternFilter implements UserFilterStrategy {
    @Override
    public boolean check(User user, UserFilterDto filter) {
        return (filter.getExperienceMax() != null && user.getExperience() > filter.getExperienceMax());
    }
}