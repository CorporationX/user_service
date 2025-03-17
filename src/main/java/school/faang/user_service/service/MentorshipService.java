package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.dto.mentorship.MentorshipDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final UserRepository userRepository;
    private final MentorshipMapper mentorshipMapper;

    public List<MentorshipDto> getMentees(long userId) {
        return getUsersById(userId, User::getMentees);
    }

    public List<MentorshipDto> getMentors(long userId) {
        return getUsersById(userId, User::getMentors);
    }

    private List<MentorshipDto> getUsersById(long userId, Function<User, List<User>> mapperFunction) {
        return userRepository.findById(userId)
                .map(mapperFunction)
                .map(mentorshipMapper::toDtos)
                .orElse(List.of());
    }

    @Transactional
    public void deleteMentee(long menteeId, long mentorId) {
        deleteMentorship(menteeId, mentorId);
    }

    @Transactional
    public void deleteMentor(long menteeId, long mentorId) {
        deleteMentorship(menteeId, mentorId);
    }

    private void deleteMentorship(long menteeId, long mentorId) {
        User mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> new EntityNotFoundException("Mentor not found."));
        User mentee = userRepository.findById(menteeId)
                .orElseThrow(() -> new EntityNotFoundException("Mentee not found."));
        if (!mentor.getMentees().remove(mentee)) {
            throw new EntityNotFoundException("The mentee was not found in the mentor's list.");
        }
        mentee.getMentors().remove(mentor);
        userRepository.save(mentor);
        userRepository.save(mentee);
    }
}
