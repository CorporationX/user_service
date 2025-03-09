package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorship.MenteeDto;
import school.faang.user_service.dto.mentorship.MentorDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.mentorship.UserNotFoundException;
import school.faang.user_service.mapper.mentorship.MenteeMapper;
import school.faang.user_service.mapper.mentorship.MentorMapper;
import school.faang.user_service.message.mentorship.MentorshipMessage;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final MentorMapper mentorMapper;
    private final MenteeMapper menteeMapper;

    public List<MenteeDto> getMentees(long userId) {
        log.debug(MentorshipMessage.GET_MENTEES_START.getMessage(), userId);
        User user = mentorshipRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        MentorDto mentorDto = mentorMapper.toDto(user);

        if (mentorDto.getMentees() == null) {
            log.debug(MentorshipMessage.EMPTY_MENTEES.getMessage(), userId);
            return new ArrayList<>();
        }
        log.debug(MentorshipMessage.GET_MENTEES_FINISH.getMessage(), userId);
        return mentorDto.getMentees();
    }

    public List<MentorDto> getMentors(long userId) {
        log.debug(MentorshipMessage.GET_MENTORS_START.getMessage(), userId);
        User user = mentorshipRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        MenteeDto menteeDto = menteeMapper.toDto(user);

        if (menteeDto.getMentors() == null) {
            log.debug(MentorshipMessage.EMPTY_MENTORS.getMessage(), userId);
            return new ArrayList<>();
        }
        log.debug(MentorshipMessage.GET_MENTORS_FINISH.getMessage(), userId);
        return menteeDto.getMentors();
    }
}
