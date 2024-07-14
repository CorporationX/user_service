package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorship.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.NotFoundException;
import school.faang.user_service.mapper.mentorship.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;


import java.util.ArrayList;
import java.util.List;

import static school.faang.user_service.exception.ErrorMessage.*;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    public List<UserDto> getMentees(long userId) {
        List<User> user = mentorshipRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND))
                .getMentees();
        return userMapper.toDtoList(user);
    }

    public List<UserDto> getMentors(long userId) {
        List<User> user = mentorshipRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND))
                .getMentors();
        return userMapper.toDtoList(user);
    }

    public void deleteMentee(long menteeId, long mentorId) {
        User mentor = mentorshipRepository.findById(mentorId)
                .orElseThrow(() -> new NotFoundException(MENTOR_NOT_FOUND));
        List<User> immutableMentees = mentor.getMentors();
        User mentee = immutableMentees.stream()
                .filter(user -> user.getId() == menteeId)
                .findAny()
                .orElseThrow(() -> new NotFoundException(MENTEE_NOT_FOUND));
        List<User> mentees = new ArrayList<>(immutableMentees);
        mentees.remove(mentee);
        mentor.setMentors(mentees);
        mentorshipRepository.save(mentor);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        User mentee = mentorshipRepository.findById(menteeId)
                .orElseThrow(() -> new NotFoundException(MENTEE_NOT_FOUND));
        List<User> immutableMentors = mentee.getMentors();
        User mentor = immutableMentors.stream()
                .filter(user -> user.getId() == mentorId)
                .findAny()
                .orElseThrow(() -> new NotFoundException(MENTOR_NOT_FOUND));
        List<User> mentors = new ArrayList<>(immutableMentors);
        mentors.remove(mentor);
        mentee.setMentors(mentors);
        mentorshipRepository.save(mentee);
    }
}
