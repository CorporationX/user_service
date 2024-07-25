package school.faang.user_service.dto.userDto;

import lombok.Data;

import java.util.List;

@Data
public class UserDto {
    private long id;
    private String username;
    private String email;
    private String phone;
    private boolean active;
    private String city;
    private Integer experience;
    private List<Long> followersId;
    private List<Long> menteesId;
    private List<Long> goalsId;
    private List<Long> skillsId;
    private long premiumId;
}
