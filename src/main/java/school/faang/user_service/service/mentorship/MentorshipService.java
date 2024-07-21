package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    public List<UserDto> getMentees(long mentorId) {
        User mentor = mentorshipRepository.findById(mentorId).orElse(null);
        List<UserDto> userMenteesDto = mentor != null ? mentor.getMentees().stream()
                .map(userMapper::toDto).toList() : new ArrayList<>();
        return userMenteesDto;
    }

    public List<UserDto> getMentors(long menteeId) {
        User mentee = mentorshipRepository.findById(menteeId).orElse(null);
        List<UserDto> userMentorsDto = mentee != null ? mentee.getMentors().stream()
                .map(userMapper::toDto).toList() : new ArrayList<>();

        return userMentorsDto;
    }

    public void deleteMentee(long menteeId, long mentorId) {
        User mentor = mentorshipRepository.findById(mentorId).orElseThrow(() ->
                new RuntimeException("Mentor not found for mentor ID: " + mentorId));
        User mentee = mentor.getMentees()
                .stream()
                .filter(m -> m.getId() == menteeId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Mentee not found for mentee ID: " + menteeId));

        mentor.getMentees().remove(mentee);
        mentorshipRepository.save(mentor);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        User mentee = mentorshipRepository.findById(menteeId).orElseThrow(() ->
                new RuntimeException("Mentee not found for mentee ID: " + menteeId));
        User mentorFoDelete = mentee.getMentors()
                .stream()
                .filter(m -> m.getId() == mentorId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Mentor not found for mentor ID: " + mentorId));

        mentee.getMentors().remove(mentorFoDelete);
        mentorshipRepository.save(mentee);
    }

    public User stopMentorship(User user) {
        user.getMentees()
                .forEach(mentee -> mentee.setMentors(
                        mentee.getMentors()
                                .stream()
                                .peek(mentor -> mentorsFilterAndPeek(user, mentor))
                                .toList()
                ));
        return user;
    }

    private static void mentorsFilterAndPeek(User user, User mentor) {
        if (mentor.getId() == user.getId()) {
            mentor.setId(user.getId());
        }
    }
}