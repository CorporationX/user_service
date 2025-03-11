package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import school.faang.user_service.entity.User;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for {@link User}
 */
@Builder
@Getter
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private boolean active;
    private String aboutMe;
    private Long countryId;
    private String city;
    private Integer experience;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Long> followersIds;
    private List<Long> followeesIds;
    private List<Long> menteesIds;
    private List<Long> mentorsIds;
}