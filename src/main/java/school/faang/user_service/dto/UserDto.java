package school.faang.user_service.dto;

import lombok.Data;
import school.faang.user_service.entity.Country;

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
    private String city;
    private Integer experience;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Long> followersId;
    private List<Long> menteesId;
    private List<Long> goalsId;
    private List<Long> skillsId;
    private long premiumId;
}
