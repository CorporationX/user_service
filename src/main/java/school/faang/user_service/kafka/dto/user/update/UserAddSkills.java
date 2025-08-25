package school.faang.user_service.kafka.dto.user.update;

import school.faang.user_service.kafka.dto.skill.SkillFilterDto;

import java.util.List;

public record UserAddSkills(
        long id,
        List<SkillFilterDto> skills
) implements UserUpdateEvent {
    private static final String TYPE = "USER_ADD_SKILLS";

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public long getId() {
        return id;
    }
}
