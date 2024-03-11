package school.faang.user_service.dto;

import school.faang.user_service.entity.User;

import java.util.List;

public class UserDto {
    private long id;
    private String name;
    private List<User> mentees;
    private List<User> mentors;
}
