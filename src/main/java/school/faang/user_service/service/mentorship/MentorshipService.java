package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filters.user.UserFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MentorshipService {

    private final MentorshipRepository mentorshipRepository;
    private final List<UserFilter> userFilters;
    private final UserMapper userMapper;

    public List<UserDto> getMentees(Long userId, UserFilterDto filters) {
        User mentor = findUserById(userId);
        Stream<User> mentees = new ArrayList<>(mentor.getMentees()).stream();
        return userFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(mentees,
                        (stream, filter) -> filter.apply(stream, filters),
                        (s1, s2) -> s1)
                .map(userMapper::toDto)
                .toList();
    }

    public List<UserDto> getMentors(Long userId, UserFilterDto filters) {
        User mentee = findUserById(userId);
        Stream<User> mentors = new ArrayList<>(mentee.getMentors()).stream();
        return userFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(mentors,
                        (stream, filter) -> filter.apply(stream, filters),
                        (s1, s2) -> s1)
                .map(userMapper::toDto)
                .toList();
    }

    public boolean deleteMentee(Long menteeId, Long mentorId) {
        User mentor = findUserById(mentorId);
        List<User> mentees = mentor.getMentees();
        boolean result = mentees.removeIf(mentee -> Objects.equals(mentee.getId(), menteeId));
        if (result) {
            mentorshipRepository.save(mentor);
            return true;
        }
        return false;
    }

    public boolean deleteMentor(Long menteeId, Long mentorId) {
        User mentee = findUserById(menteeId);
        List<User> mentors = mentee.getMentors();
        boolean result = mentors.removeIf(mentor -> Objects.equals(mentor.getId(), mentorId));
        if (result) {
            mentorshipRepository.save(mentee);
            return true;
        }
        return false;
    }

    private User findUserById(Long userId) {
        if (userId < 0) {
            throw new DataValidationException("User's id cannot be less than zero");
        }

        return mentorshipRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("User not found"));
    }
}
