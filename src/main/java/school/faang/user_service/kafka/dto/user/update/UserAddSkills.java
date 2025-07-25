package school.faang.user_service.kafka.dto.user.update;

import java.util.List;

public record UserAddSkills(
        long id,
        List<Long> skillIds
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
