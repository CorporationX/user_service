package school.faang.user_service.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserDto {
    private long id;
    private String username;
    private String email;
    private String phone;
    private String password;
    private boolean active;
    private String aboutMe;
    private long countryId;
    private String city;
    private Integer experience;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Long> ownedEventsIds;
    private List<Long> menteesIds;
    private List<Long> mentorsIds;
    private List<Long> setGoalsIds;
    private List<Long> goalsIds;
}
