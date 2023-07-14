package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentee.MenteeDto;
import school.faang.user_service.mapper.MenteeMapper;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final MenteeMapper menteeMapper;

    public List<MenteeDto> getMentees(long userId) {
        if (!mentorshipRepository.existsById(userId)) {
            throw new RuntimeException("User with id not found");
        }
        List<User> userList = mentorshipRepository.getAllByMentorId(userId);
        return menteeMapper.toUserDto(userList);
    }
}
