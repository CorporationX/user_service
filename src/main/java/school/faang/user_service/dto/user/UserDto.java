package school.faang.user_service.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode(of = {"id","username"})
public class UserDto {
    private Long id;
    private String username;
    private String password;
    private String email;
    private Boolean active;
    private List<Long> goalIds;
    private List<Long> participatedEventIds;
    private List<Long> menteeIds;
}
