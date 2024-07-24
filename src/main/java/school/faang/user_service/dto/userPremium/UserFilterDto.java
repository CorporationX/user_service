package school.faang.user_service.dto.userPremium;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFilterDto {
    private String usernameFilter;
    private String countryFilter;
}
