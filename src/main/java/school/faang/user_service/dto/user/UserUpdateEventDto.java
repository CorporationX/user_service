package school.faang.user_service.dto.user;

import lombok.Data;

@Data
public class UserUpdateEventDto {
    private Long id;
    private Type type;
}
