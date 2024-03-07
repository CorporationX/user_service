package school.faang.user_service.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
public class UserDto {
    private String username;
    private long id;
    private List<Long> menteesIds;
    private List<Long> mentorsIds;
}
