package school.faang.user_service.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserFilterDto {
    private String namePattern;
    private String emailPattern;
    private String phonePattern;
    private String aboutPattern;
}
