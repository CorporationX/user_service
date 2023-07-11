package school.faang.user_service.dto.mentorship;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MenteeDto {
    // Не разобрался какие поля необходимо добавить в дтошку, поэтому выбрал несколько произвольных
    private String username;
    private String email;
    private String phone;
    private String aboutMe;
}
