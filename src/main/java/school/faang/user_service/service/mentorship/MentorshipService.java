package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import school.faang.user_service.entity.dto.UserDto;
import school.faang.user_service.mapper.mentorship.MentorshipMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentorshipService {

    private final MentorshipRepository mentorshipRepository;
    private final MentorshipMapper mentorshipMapper;

    private void checkParam(long... ids) {
        for (long id : ids) {
            if (id <= 0) {
                log.warn(id + "not found");
                throw new IllegalArgumentException("Id not found");
            }
        }
    }

    public List<UserDto> getMentees(long mentorId) {
        log.info("Fetching mentees for mentor with ID: {}", mentorId);
        checkParam(mentorId);
        return mentorshipRepository.findById(mentorId)
                .map(mentor -> mentor.getMentees().stream()
                        .map(mentorshipMapper::toDto)
                        .toList())
                .orElseGet(() -> {
                    log.warn("No mentor found with ID: {}", mentorId);
                    return List.of();
                });
    }

    public List<UserDto> getMentors(long menteeId) {
        log.info("Fetching mentors for mentee with ID: {}", menteeId);
        checkParam(menteeId);
        return mentorshipRepository.findById(menteeId)
                .map(mentee -> mentee.getMentors().stream()
                        .map(mentorshipMapper::toDto)
                        .toList())
                .orElseGet(() -> {
                    log.warn("No mentee found with ID: {}", menteeId);
                    return List.of();
                });
    }

    public void deleteMentee(long menteeId, long mentorId) {
        log.info("Deleting mentorship relationship for mentor ID: {} and mentee ID: {}", mentorId, menteeId);
        checkParam(mentorId, menteeId);
        mentorshipRepository.deleteMentorship(menteeId, mentorId);
        log.info("Deleted mentorship relationship for mentor ID: {} and mentee ID: {}", mentorId, menteeId);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        log.info("Deleting mentorship relationship for mentor ID: {} and mentee ID: {}", mentorId, menteeId);
        checkParam(mentorId, menteeId);
        mentorshipRepository.deleteMentorship(menteeId, mentorId);
        log.info("Deleted mentorship relationship for mentor ID: {} and mentee ID: {}", mentorId, menteeId);
    }
}

