package school.faang.user_service.service.user.filter;

import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class UserFilterBySkills implements UserFilter{
    @Override
    public boolean isApplicable(UserFilterDto filterDto) {
        return Objects.nonNull(filterDto.getSkills());
    }

    @Override
    public boolean test(User user, UserFilterDto filterDto) {
        List<Long> userSkillIds = user.getSkills().stream()
                .map(Skill::getId)
                .toList();

        Set<Long> filterSkillIds = filterDto.getSkills().stream()
                .map(SkillDto::getId)
                .collect(Collectors.toSet());

        return userSkillIds.containsAll(filterSkillIds);
    }
}
