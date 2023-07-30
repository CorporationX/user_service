package school.faang.user_service.dto.mentorshipRequest;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class RequestsResponse {
    List<MentorshipRequestDto> mentorshipRequestDtos;
}
