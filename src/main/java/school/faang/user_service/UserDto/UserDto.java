package school.faang.user_service.UserDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.event.Event;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;
    private String username;
    private List<UserDto> mentors;
    private List<UserDto> mentees;
}