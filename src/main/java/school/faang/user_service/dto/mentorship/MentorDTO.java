package school.faang.user_service.dto.mentorship;

import lombok.Data;

import java.util.List;

@Data
public class MentorDTO {
    private Long id;
    private String name;
    private List<Long> menteesIds;
}