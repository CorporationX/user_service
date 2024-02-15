package school.faang.user_service.dto;

import lombok.Data;

@Data
public class MentorshipOfferedEvent {
    private long id;
    private long authorId;
    private long mentorId;
}
