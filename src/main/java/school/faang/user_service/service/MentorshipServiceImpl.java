package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.MentorshipDeleteDto;
import school.faang.user_service.dto.SuccessResponseDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.Mentorship;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.MentorshipNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.MentorshipRepository;

import java.util.List;
import java.util.function.BiFunction;

@Service
@RequiredArgsConstructor
public class MentorshipServiceImpl implements MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public void createMentorship(long mentorId, long menteeId) {
        User mentor = userService.getReferenceById(mentorId);
        User mentee = userService.getReferenceById(menteeId);

        Mentorship mentorship = new Mentorship();
        mentorship.setMentor(mentor);
        mentorship.setMentee(mentee);
        mentorshipRepository.save(mentorship);
    }

    @Override
    public boolean existsByMentorIdAndMenteeId(long mentorId, long menteeId) {
        return mentorshipRepository.existsByMentorIdAndMenteeId(mentorId, menteeId);
    }

    @Override
    public Long findMentorshipConnectionId(long mentorId, long menteeId) {
        return mentorshipRepository.findIdByMentorIdAndMenteeId(mentorId, menteeId)
                .orElseThrow(() -> new MentorshipNotFoundException(
                        "No mentorship relationship found for mentor %d and mentee %d".formatted(mentorId, menteeId)));
    }

    @Override
    public List<UserDto> getMentees(long mentorId) {
        List<User> mentees = mentorshipRepository.findAllMenteesByMentorId(mentorId);
        return mentees.stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public List<UserDto> getMentors(long menteeId) {
        List<User> mentors = mentorshipRepository.findAllMentorsByMenteeId(menteeId);
        return mentors.stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public SuccessResponseDto deleteMentee(MentorshipDeleteDto mentorshipDeleteDto) {
        return deleteMentorship(mentorshipDeleteDto, (mentorId, menteeId) ->
                "Mentee with ID %d successfully deleted from mentor with ID %d".formatted(menteeId, mentorId));
    }

    @Override
    @Transactional
    public SuccessResponseDto deleteMentor(MentorshipDeleteDto mentorshipDeleteDto) {
        return deleteMentorship(mentorshipDeleteDto,
                "Mentor with ID %d successfully deleted from mentee with ID %d"::formatted);
    }

    private SuccessResponseDto deleteMentorship(MentorshipDeleteDto dto,
                                                BiFunction<Long, Long, String> messageGenerator) {
        long mentorId = dto.mentorId();
        long menteeId = dto.menteeId();
        long mentorshipId = findMentorshipConnectionId(mentorId, menteeId);
        mentorshipRepository.deleteById(mentorshipId);
        String message = messageGenerator.apply(mentorId, menteeId);
        return new SuccessResponseDto(message);
    }
}
