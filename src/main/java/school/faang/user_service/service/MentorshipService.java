package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final  UserRepository userRepository;

    public List<User> getMentees(Long userId) {
        if (mentorshipRepository.findById(userId).isPresent()){
            return mentorshipRepository.findById(userId).get().getMentees();
        } else {throw new EntityNotFoundException("Invalid mentee ID");}
    }

    public List<User> getMentors(Long userId) {
        if (mentorshipRepository.findById(userId).isPresent()) {
            return mentorshipRepository.findById(userId).get().getMentors();
        } else {throw new EntityNotFoundException("Invalid mentor ID");}
    }

    public void deleteMentee(Long menteeId, Long mentorId) {
        User mentee = userRepository.findById(menteeId).orElseThrow(() ->
                new EntityNotFoundException("Invalid mentee ID"));
        User mentor = userRepository.findById(mentorId).orElseThrow(() ->
                new EntityNotFoundException("Invalid mentor ID"));

        if (mentor.getId() == mentee.getId()) {
            throw new IllegalArgumentException("Invalid deletion. You can't be mentee of yourself");
        }

        mentor.getMentees().remove(mentee);
        userRepository.save(mentor);
    }

    public void deleteMentor(Long mentorId, Long menteeId) {
        User mentee = userRepository.findById(menteeId).orElseThrow(() ->
                new EntityNotFoundException("Invalid mentee ID"));
        User mentor = userRepository.findById(mentorId).orElseThrow(() ->
                new EntityNotFoundException("Invalid mentor ID"));

        if (mentor.getId() == mentee.getId()) {
            throw new IllegalArgumentException("Invalid deletion. You can't be mentor of yourself");
        }

        mentee.getMentees().remove(mentor);
        userRepository.save(mentee);
    }
}

