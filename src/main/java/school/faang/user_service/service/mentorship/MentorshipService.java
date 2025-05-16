package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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


    public List<GetMenteesResponse> getMentees(long userId) {
        List<User> mentees = mentorshipRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Mentor not found")).getMentees();
        return mentees.stream()
                .map(menteeMapper::toDto)
                .toList();
    }

    public List<GetMentorsResponse> getMentors(long userId) {
        List<User> mentors = mentorshipRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found")).getMentors();
        return mentors.stream()
                .map(mentorsMapper::toDto)
                .toList();
    }

    public void deleteMentee(long menteeId, long mentorId) {
        User mentor = mentorshipRepository.findById(mentorId)
                .orElseThrow(() -> new EntityNotFoundException("Mentor not found"));
        User mentee = mentorshipRepository.findById(menteeId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        mentor.getMentees().remove(mentee);
        mentee.getMentors().remove(mentor);
        mentorshipRepository.save(mentee);
    }

    @Transactional
    public void deleteMentor(long menteeId, long mentorId) {
        deleteMentee(menteeId, mentorId);
    }
}