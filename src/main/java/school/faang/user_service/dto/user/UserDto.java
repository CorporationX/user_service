package school.faang.user_service.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.contact.PreferredContact;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String aboutMe;
    private String email;
    private List<Long> menteeIds;
    private List<Long> mentorIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private PreferredContact preference;
}
