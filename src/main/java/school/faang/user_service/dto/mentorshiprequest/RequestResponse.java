package school.faang.user_service.dto.mentorshiprequest;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class RequestResponse {

    List<MentorshipRequestDto> dtos;
}
