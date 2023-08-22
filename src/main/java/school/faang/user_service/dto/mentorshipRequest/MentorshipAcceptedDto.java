package school.faang.user_service.dto.mentorshipRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MentorshipAcceptedDto {

    private Integer id;
    private Integer receiverId;
    private Integer requesterId;
}
