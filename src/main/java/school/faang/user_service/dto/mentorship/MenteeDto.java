package school.faang.user_service.dto.mentorship;

import lombok.Data;

import java.util.List;

@Data
public class MenteeDto {
    private long id;
    private String username;
    private List<MentorDto> mentors;
}
