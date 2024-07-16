package school.faang.user_service.dto.mentorship;

import lombok.Data;

import java.util.List;

@Data
public class UserDto {

    private long id;

    private String username;

    private String email;

    private String phone;

    private boolean active;

    private List<Long> menteeIds;

    private List<Long> mentorIds;

}
