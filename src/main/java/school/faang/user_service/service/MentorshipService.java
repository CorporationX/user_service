package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.validator.MentorshipValidator;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipValidator mentorshipValidator;
    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public List<UserDto> getMentees(long mentorId) {
        Optional<User> mentorOptional = mentorshipRepository.findById(mentorId);
        User mentor = mentorOptional.orElseThrow(() -> new UserNotFoundException(mentorId));
        return userMapper.toDtoList(mentor.getMentees());
    }

    @Transactional(readOnly = true)
    public List<UserDto> getMentors(long menteeId) {
        Optional<User> menteeOptional = mentorshipRepository.findById(menteeId);
        User mentee = menteeOptional.orElseThrow(() -> new UserNotFoundException(menteeId));
        return userMapper.toDtoList(mentee.getMentors());
    }

    @Transactional
    public void deleteMentee(long menteeId, long mentorId) {
        mentorshipValidator.validateMenteeAndMentorIds(menteeId, mentorId);
        mentorshipRepository.findById(mentorId)
                .orElseThrow(() -> new UserNotFoundException(mentorId))
                .getMentees().removeIf(mentee -> mentee.getId() == menteeId);
    }

    @Transactional
    public void deleteMentor(long menteeId, long mentorId) {
        mentorshipValidator.validateMenteeAndMentorIds(menteeId, mentorId);
        mentorshipRepository.findById(menteeId)
                .orElseThrow(() -> new UserNotFoundException(menteeId))
                .getMentors().removeIf(mentor -> mentor.getId() == mentorId);
    }
}