package school.faang.user_service.controller.mentorship;

import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.MentorshipService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class MentorshipServiceImpl implements MentorshipService {

    private UserRepository userRepository;

    @Autowired
    public void MentorshipService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public MentorshipServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @NonNull
    public List<User> getMentees(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

        List<User> userMentees = new ArrayList<>();
        Optional<List<User>> mentees = Optional.ofNullable(user.getMentees());
        mentees.ifPresent(userMentees::addAll);
        return userMentees;
    }

    @NonNull
    public List<User> getMentors(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

        List<User> userMentors = new ArrayList<>();
        Optional<List<User>> mentors = Optional.ofNullable(user.getMentors());
        mentors.ifPresent(userMentors::addAll);
        return userMentors;
    }

    public void deleteMentee(long menteeId, long mentorId) {
        User mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> new EntityNotFoundException("Mentor with id " + mentorId + " not found"));

        mentor.getMentees().removeIf(menteeToRemove -> menteeToRemove.getId().equals(menteeId));
    }

    public void deleteMentor(long menteeId, long mentorId) {
        User mentee = userRepository.findById(menteeId)
                .orElseThrow(() -> new EntityNotFoundException("Mentee with id " + menteeId + " not found"));

        mentee.getMentors().removeIf(mentorToRemove -> mentorToRemove.getId().equals(mentorId));

    }
}
