package school.faang.user_service.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String country;
    private String city;
    private Integer experience;
    private List<Long> skillIds;
}
