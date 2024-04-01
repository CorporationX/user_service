package school.faang.user_service.service.mentorship;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.MentorshipStartEventDto;
import school.faang.user_service.dto.mentorship.MentorshipDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.Mentorship;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.mentorship.MentorshipMapper;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.publisher.MentorshipEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final MentorshipRepository mentorshipRepository;
    private final MentorshipMapper mentorshipMapper = Mappers.getMapper(MentorshipMapper.class);
    private final MentorshipEventPublisher mentorshipEventPublisher;

    @Transactional
    public MentorshipDto create(MentorshipDto mentorshipDto) {
        Mentorship mentorship = mentorshipRepository.save(mentorshipMapper.toMentorship(mentorshipDto));
        MentorshipStartEventDto mentorshipStartEventDto = MentorshipStartEventDto.builder()
                .mentorId(mentorship.getMentor().getId())
                .menteeId(mentorship.getMentee().getId())
                .build();
        mentorshipEventPublisher.publish(mentorshipStartEventDto);
        return mentorshipMapper.toDto(mentorship);
    }

    @Transactional
    public List<UserDto> getMentees(long userId) {
        return getUser(userId).getMentees().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional
    public List<UserDto> getMentors(long userId) {
        return getUser(userId).getMentors().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional
    public void deleteMentee(long menteeId, long mentorId) {
        User mentee = getUser(menteeId);
        User mentor = getUser(mentorId);
        mentor.getMentees().remove(mentee);
        userRepository.save(mentor);
    }

    @Transactional
    public void deleteMentor(long menteeId, long mentorId) {
        User mentee = getUser(menteeId);
        User mentor = getUser(mentorId);
        mentee.getMentors().remove(mentor);
        userRepository.save(mentee);
    }

    private User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));
    }
}
