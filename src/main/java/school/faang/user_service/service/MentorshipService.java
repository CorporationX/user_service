package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.catalina.security.SecurityUtil.remove;
import static org.springframework.data.redis.connection.lettuce.LettuceConverters.toInt;


@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final UserRepository userRepository;

    public List<User> getMenteesOfUser(Long userId) {
        User user = findUserById(userId);
        return user.getMentees();
    }

    public List<User> getMentorsOfUser(Long userId) {
        User user = findUserById(userId);
        return user.getMentors();
    }

    public void deleteMentee(Long menteeId, Long mentorId) {
        User mentor = findUserById(mentorId);
        User mentee = findUserById(menteeId);

        if (mentee.getId() == mentor.getId()) {
            throw new IllegalArgumentException("Invalid deletion. You can't be mentee of yourself");
        }

        mentor.getMentees().remove(mentee);
        userRepository.save(mentor);
    }

    public void deleteMentor(Long menteeId, Long mentorId) {
        User mentee = findUserById(menteeId);
        User mentor = findUserById(mentorId);

        if (mentor.getId() == mentee.getId()) {
            throw new IllegalArgumentException("Invalid deletion. You can't be mentor of yourself");
        }

        mentee.getMentors().remove(mentor);
        userRepository.save(mentee);
    }

    private User findUserById(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(()->new EntityNotFoundException("Invalid user Id"));
    }

  public int cancelMentoring(Long userId) {
      List<User> mentees = getMenteesOfUser(userId);

      mentees.forEach(mentee -> {
          List<User> mentors = mentee.getMentors();
          mentee.setMentors(mentors.stream().filter(mentor -> mentor.getId() != userId).toList());
      });

      return mentees.size();
  }
}

