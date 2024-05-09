package school.faang.user_service.dto.user;

import lombok.Data;
import school.faang.user_service.entity.premium.Premium;

@Data
public class UserDto {
    private long id;
    private String city;
    private Integer experience;
    private String username;
    private Premium premium;
}
