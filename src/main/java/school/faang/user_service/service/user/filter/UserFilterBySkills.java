package school.faang.user_service.service.user.filter;

import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.Objects;

public class UserFilterBySkills implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filterDto) {
        return Objects.nonNull(filterDto.getSkillIds());
    }

    @Override
    public List<User> filterUsers(List<User> users, UserFilterDto filterDto) {
        List<Long> requiredSkillIds = filterDto.getSkillIds();
        return users.stream()
                .filter(user -> skillsToIds(user.getSkills()).containsAll(requiredSkillIds))
                .toList();
    }

    private List<Long> skillsToIds(List<Skill> skills) {
        return skills.stream()
                .map(Skill::getId)
                .toList();
    }
}

