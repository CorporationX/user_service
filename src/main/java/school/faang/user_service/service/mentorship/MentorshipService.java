package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class MentorshipService {

    private static final String MENTOR_NOT_FOUND_MESSAGE = "Mentor with id %d not found";
    private static final String MENTEE_NOT_FOUND_MESSAGE = "Mentee with id %d not found";
    private static final String MENTORSHIP_NOT_FOUND_MESSAGE = "Mentorship between mentor %d and mentee %d not found";

    private final UserRepository userRepository;
    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;


    public List<UserDto> getMentees(@NotNull @Positive Long mentorId) {
        return userRepository.findById(mentorId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(MENTOR_NOT_FOUND_MESSAGE, mentorId)))
                .getMentees()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    public List<UserDto> getMentors(@NotNull @Positive Long menteeId) {
        return userRepository.findById(menteeId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(MENTEE_NOT_FOUND_MESSAGE, menteeId)))
                .getMentors()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    public void deleteByMentorIdAndMenteeId(@NotNull @Positive Long mentorId, @NotNull @Positive Long menteeId) {
        if (mentorshipRepository.existsByMentorIdAndMenteeId(mentorId, menteeId)) {
            mentorshipRepository.deleteByMentorIdAndMenteeId(mentorId, menteeId);
        } else {
            throw new EntityNotFoundException(
                    String.format(MENTORSHIP_NOT_FOUND_MESSAGE, mentorId, menteeId)
            );
        }
    }

}
