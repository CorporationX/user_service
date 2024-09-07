package school.faang.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.MentorshipRepository;
import school.faang.user_service.entity.User;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MentorshipService {

    @Autowired
    private MentorshipRepository mentorshipRepository;

    public List<MenteeDTO> getMentees(long userId) {
        List<User> mentees = mentorshipRepository.findMenteesByMentorId(userId);
        return mentees.stream()
                .map(MenteeMapper.INSTANCE::menteeToMenteeDTO)
                .collect(Collectors.toList());
    }

    public void deleteMentee(long menteeId, long mentorId) {
        User mentee = mentorshipRepository.findMenteeByMentorIdAndMenteeId(mentorId, menteeId);
        if (mentee != null) {
            mentorshipRepository.delete(mentee);
        }
    }

    public void deleteMentor(long menteeId, long mentorId) {
        User mentor = mentorshipRepository.findMentorByMenteeIdAndMentorId(menteeId, mentorId);
        if (mentor != null) {
            mentorshipRepository.delete(mentor);
        }
    }
}