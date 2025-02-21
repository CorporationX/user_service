package school.faang.user_service.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.entity.User;
import school.faang.user_service.dto.users.UserDto;
import school.faang.user_service.mapper.MentorshipMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.MentorshipService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MentorshipServiceImpl implements MentorshipService {

    private final MentorshipRepository mentorshipRepository;
    private final MentorshipMapper mentorshipMapper;

    @Override
    public List<UserDto> getMentees(long userId) {
        final Optional<User> UserById = mentorshipRepository.findById(userId);
        if (UserById.isPresent()) {
            final List<User> mentees = UserById.get().getMentees();
            return mentorshipMapper.toUserDto(mentees);
        }
        return Collections.emptyList();
    }

    @Override
    public List<UserDto> getMentors(long userId) {
        final User UserById = mentorshipRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с таким userID не найден"));
        List<User> mentors = UserById.getMentors();
        if (mentors == null) {
            mentors = Collections.emptyList();
        }
        return mentorshipMapper.toUserDto(mentors);
    }

    @Override
    public void deleteMentee(long menteeId, long mentorId) {

        User mentor = mentorshipRepository.findById(mentorId)
                .orElseThrow(() -> new EntityNotFoundException("Ментор с ID " + mentorId + " не найден"));

        List<User> mentees = mentor.getMentees();
        if (mentees == null) {
            mentees = new ArrayList<>();
            mentor.setMentees(mentees);
        }

        User userMentee = null;
        for (User mentee : mentees) {
            if (mentee.getId() == menteeId) {
                userMentee = mentee;
                break;
            }
        }

        if (userMentee == null) {
            throw new EntityNotFoundException("Менти с ID " + menteeId + " не найден у ментора с ID " + mentorId);
        }

        mentees.remove(userMentee);

        mentorshipRepository.save(mentor);
    }

    @Override
    public void deleteMentor(long menteeId, long mentorId) {

        User mentee = mentorshipRepository.findById(menteeId)
                .orElseThrow(() -> new EntityNotFoundException("Менти с ID " + menteeId + " не найден"));

        List<User> mentors = mentee.getMentors();
        if (mentors == null) {
            mentors = new ArrayList<>();
            mentee.setMentors(mentors);
        }

        User userMentor = null;
        for (User mentor : mentors) {
            if (mentor.getId() == mentorId) {
                userMentor = mentor;
                break;
            }
        }

        if (userMentor == null) {
            throw new EntityNotFoundException("Ментор с ID " + mentorId + " не найден у менти с ID " + menteeId);
        }

        mentors.remove(userMentor);

        mentorshipRepository.save(mentee);
    }
}
