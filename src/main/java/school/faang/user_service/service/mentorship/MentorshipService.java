package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorship.GetMenteesResponse;
import school.faang.user_service.dto.mentorship.GetMentorsResponse;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.mentorship.MenteeMapper;
import school.faang.user_service.mapper.mentorship.MentorsMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {

    private final MentorshipRepository mentorshipRepository;
    private final MenteeMapper menteeMapper;
    private final MentorsMapper mentorsMapper;

    private User findById(long userId) {
        return mentorshipRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + userId));
    }

    public List<GetMenteesResponse> getMentees(long userId) {
        List<User> mentees = findById(userId).getMentees();
        return mentees.stream()
                .map(menteeMapper::toDto)
                .toList();
    }

    public List<GetMentorsResponse> getMentors(long userId) {
        List<User> mentors = findById(userId).getMentors();
        return mentors.stream()
                .map(mentorsMapper::toDto)
                .toList();
    }

    public void deleteMentee(long menteeId, long mentorId) {
        User mentor = findById(mentorId);
        User mentee = findById(menteeId);
        mentor.getMentees().remove(mentee);
        mentee.getMentors().remove(mentor);
        mentorshipRepository.save(mentee);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        deleteMentee(menteeId, mentorId);
    }
}