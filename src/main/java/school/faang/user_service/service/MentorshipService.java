package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    public List<UserDto> getMentees(long userId) {
        User user = mentorshipRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("This mentor with id: " + userId + " is not in the database"));
        return user.getMentees().stream().map((mentee)->userMapper.toDto(mentee)).toList();
    }

    public List<UserDto> getMentors(long userId){
        User user = mentorshipRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("This user with id: " + userId + " is not in the database"));
        return user.getMentors().stream().map((mentor)->userMapper.toDto(mentor)).toList();
    }

    public void deleteMentee(long menteeId, long mentorId){
        List<UserDto> mentees = getMentees(mentorId);
        UserDto userDto = mentees.stream().filter(mentee->mentee.getId()==menteeId).findAny().orElseThrow(()->
            new IllegalArgumentException("A mentor with an id: "+ mentorId +" does not have a mentee with an id: " + menteeId)
        );

    }


}
