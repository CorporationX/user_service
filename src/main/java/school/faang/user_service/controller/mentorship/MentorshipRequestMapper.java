package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.user.MentorshipRequest;
import school.faang.user_service.mapper.UserMapper;

@Component
@RequiredArgsConstructor
public class MentorshipRequestMapper {
    private UserMapper userMapper;



    public MentorshipRequestDto toMentorshipRequestDto(MentorshipRequest request) {
        if (request == null) {
            return null;
        }


        return new MentorshipRequestDto(request.getId(),
                request.getDescription(),
                userMapper.toUserDto(request.getRequester()),
                userMapper.toUserDto(request.getReceiver()),
                request.getStatus());
    }
}
