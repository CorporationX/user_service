package school.faang.user_service.dto.event;

import lombok.Data;

import java.util.List;

@Data
public class UserDto {
    private int id;
    private String name;
    private List<SkillDto> skills;
    private List<EventDto> events;
}
