package school.faang.user_service.kafka.dto.user.update;

import school.faang.user_service.kafka.dto.skill.SkillFilterDto;

import java.util.List;

public record UserAddSkills(
        long id,
        List<SkillFilterDto> skills
) implements UserUpdateEvent {

    @Override
    public String getType() {
        return "USER_ADD_SKILLS";
    }

    @Override
    public long getId() {
        return id;
    }
}
