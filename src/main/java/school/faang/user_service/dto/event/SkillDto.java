package school.faang.user_service.dto.event;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;

import java.util.List;

@Data
@Component
@RequiredArgsConstructor
public class SkillDto {
    private long id;
    private String title;
    private List<User> users;

}
