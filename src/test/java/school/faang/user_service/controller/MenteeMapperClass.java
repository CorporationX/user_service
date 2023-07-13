package school.faang.user_service.controller;

import school.faang.user_service.entity.User;

public class MenteeMapperClass implements MenteeMapper {
    @Override
    public MenteeDto toUser(User user) {
        return new MenteeDto("one", "email", "12345567", 1);

    }
}
