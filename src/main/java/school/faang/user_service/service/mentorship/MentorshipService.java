package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.mentorship.MenteeMentorOneUser;
import school.faang.user_service.exception.mentorship.UserNotFound;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    public List<UserDTO> getMentees(long mentorId) {
        User user = findValidUser(mentorId);

        return user.getMentees().stream().map(userMapper::toDto).toList();
    }

    public List<UserDTO> getMentors(long menteeId) {
        User user = findValidUser(menteeId);

        return user.getMentors().stream().map(userMapper::toDto).toList();
    }

    public ResponseEntity<?> deleteMentee(long menteeId, long mentorId) {
        if (menteeId == mentorId) {
            throw new MenteeMentorOneUser("a mentor cannot be a mentee of himself");
        }
        User mentor = findValidUser(mentorId);
        User mentee = findValidUser(menteeId);
        final boolean isDeleted = mentor.getMentees().remove(mentee);
        mentorshipRepository.save(mentor);

        return isDeleted
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }

    public User findValidUser(long id) {
        return mentorshipRepository.findUserById(id)
                .orElseThrow(() -> new UserNotFound("user not found in database"));
    }
}
