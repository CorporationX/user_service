package school.faang.user_service.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserTransportDto {

    private Long id;
    private String username;
    private String email;
    private String phone;
    private boolean isActive;
    private PreferredContact preference;
}