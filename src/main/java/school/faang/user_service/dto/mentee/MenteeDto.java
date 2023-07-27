package school.faang.user_service.dto.mentee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MenteeDto {
    private long id;
    private String username;
    private String email;
    private String phone;
}
