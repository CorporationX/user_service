package school.faang.user_service.dto.event;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;

import java.util.List;

@Data
@NoArgsConstructor
public class SkillDto {
    private long id;
    private String title;
    private List<Long> userId;
}
