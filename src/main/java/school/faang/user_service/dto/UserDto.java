package school.faang.user_service.dto;

=======
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
public class UserDto {
    @NotNull
    private String username;
    private long id;
    private List<Long> menteesIds;
    private List<Long> mentorsIds;
    private String email;
}
