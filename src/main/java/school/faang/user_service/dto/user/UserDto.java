package school.faang.user_service.dto.user;

import lombok.*;
import school.faang.user_service.entity.premium.Premium;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private long id;
    private String city;
    private String email;
    private Integer experience;
    private String username;
    private Premium premium;

}