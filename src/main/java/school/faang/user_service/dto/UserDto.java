package school.faang.user_service.dto;

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
    private String email;
    private String phone;
    private boolean active;
    private String aboutMe;
    private Country country;
    private String city;
    private Integer experience;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<UserDto> followers;
    private List<UserDto> followees;
    private List<Event> ownedEvents;
    private List<UserDto> mentors;
    private List<UserDto> mentees;
    private List<MentorshipRequest> receivedMentorshipRequests;
    private List<MentorshipRequest> sentMentorshipRequests;
}