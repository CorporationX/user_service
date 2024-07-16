package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.mentorship.MentorshipUserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.mentorship.MentorshipNoSuchElementException;
import school.faang.user_service.mapper.mentorship.MentorshipMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

/**
 * @author Evgenii Malkov
 */
@Service
@RequiredArgsConstructor
public class MentorshipService {

    private final MentorshipRepository mentorshipRepository;
    private final MentorshipMapper mentorshipMapper;

    @Transactional(readOnly = true)
    public List<MentorshipUserDto> getMentees(long userId) {
        return getUser(userId).getMentees().stream()
                .map(mentorshipMapper::toUserDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MentorshipUserDto> getMentors(long userId) {
        return getUser(userId).getMentors().stream()
                .map(mentorshipMapper::toUserDto)
                .toList();
    }

    @Transactional
    public boolean deleteMentorshipRelations(long mentorId, long menteeId) {
        User mentor = getUser(mentorId);
        User mentee = getUser(menteeId);
        if (!mentor.getMentees().contains(mentee)) {
            throw new MentorshipNoSuchElementException(
                    String.format("Mentor with id: %s haven't mentee with id: %s", mentorId, menteeId));
        }
        mentor.getMentees().remove(mentee);
        mentee.getMentors().remove(mentor);
        mentorshipRepository.save(mentor);
        return true;
    }

    private User getUser(long userId) {
        return mentorshipRepository.findById(userId)
                .orElseThrow(() -> new MentorshipNoSuchElementException(
                        "Not found user with id: " + userId));
    }
}
