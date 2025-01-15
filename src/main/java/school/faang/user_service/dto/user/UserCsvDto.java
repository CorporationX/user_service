package school.faang.user_service.dto.user;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.Builder;

import school.faang.user_service.entity.Country;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCsvDto {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String aboutMe;
    private String city;
    private Country country;
}