package school.faang.user_service.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MentorshipRequestDto {
    private Long requesterId;
    private Long receiverId;
    private String description;

}
