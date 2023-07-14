package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentee.MenteeDto;
import school.faang.user_service.dto.mentee.MentorDto;
import school.faang.user_service.mapper.mentee.MenteeMapper;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.mentor.MentorMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final MenteeMapper menteeMapper;
    private final MentorMapper mentorMapper;

    public List<MenteeDto> getMentees(long userId) {
        if (!mentorshipRepository.existsById(userId)) {
            throw new RuntimeException("User with id not found");
        }
        List<User> userList = mentorshipRepository.getAllByMentorId(userId);
        return menteeMapper.toUserDto(userList);
    }

    public List<MentorDto> getMentors(long userId) {
        if (!mentorshipRepository.existsById(userId)) {
            throw new RuntimeException("User with id not found");
        }
        List<User> userList = mentorshipRepository.getAllByMenteeId(userId);
        return mentorMapper.toMentorDto(userList);
    }
}
