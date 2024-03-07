package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final UserRepository userRepository;

    public List<User> getMentees(long userId) {
        if (userId == 0) {
            throw new IllegalArgumentException("Id is null");
        } else {
            Optional<User> mentorOptional = userRepository.findById(userId);
            return mentorOptional.isEmpty() ? Collections.emptyList() : mentorOptional.get().getMentees();
        }
    }

    public List<User> getMentors(long userId) {
        if (userId == 0) {
            throw new IllegalArgumentException("Id is null");
        } else {
            Optional<User> mentorOptional = userRepository.findById(userId);
            return mentorOptional.isEmpty() ? Collections.emptyList() : mentorOptional.get().getMentors();
        }
    }

    public void deleteMentee(long menteeId, long mentorId) {
        if (menteeId == 0 || mentorId == 0) {
            throw new IllegalArgumentException("Id is null");
        } else {
            Optional<User> mentorOptional = userRepository.findById(mentorId);
            mentorOptional.ifPresent(mentor -> {
                List<User> modifiableMentees = new ArrayList<>(mentor.getMentees());
                modifiableMentees.removeIf(mentee -> mentee.getId() == menteeId);
                mentor.setMentees(modifiableMentees);
            });
            mentorOptional.ifPresent(userRepository::save);
        }
    }

    public void deleteMentor(long menteeId, long mentorId) {
        if (menteeId == 0 || mentorId == 0) {
            throw new IllegalArgumentException("Id is null");
        } else {
            Optional<User> menteeOptional = userRepository.findById(menteeId);
            menteeOptional.ifPresent(mentee -> {
                List<User> modifiableMentors = new ArrayList<>(mentee.getMentors());
                modifiableMentors.removeIf(mentor -> mentor.getId() == mentorId);
                mentee.setMentees(modifiableMentors);
            });
            menteeOptional.ifPresent(userRepository::save);
        }
    }
}

