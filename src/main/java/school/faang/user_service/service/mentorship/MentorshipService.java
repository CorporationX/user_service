package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.mentorship.MentorshipDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.mentorship.MentorshipMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final MentorshipMapper mentorshipMapper;

    public List<MentorshipDto> getMentees(long mentorId) {
        var mentees = new ArrayList<MentorshipDto>();
        mentorshipRepository.findById(mentorId).ifPresent(mentor ->
                mentees.addAll(mentor.getMentees().stream()
                        .map(mentorshipMapper::toDto)
                        .toList())
        );

        return mentees;
    }

    public List<MentorshipDto> getMentors(long menteeId) {
        var mentors = new ArrayList<MentorshipDto>();
        mentorshipRepository.findById(menteeId).ifPresent(mentee ->
                mentors.addAll(mentee.getMentors().stream()
                        .map(mentorshipMapper::toDto)
                        .toList())
        );

        return mentors;
    }

    @Transactional
    public void deleteMentee(long menteeId, long mentorId) {
        mentorshipRepository.deleteMentorship(menteeId, mentorId);
    }

    @Transactional
    public void deleteMentor(long menteeId, long mentorId) {
        mentorshipRepository.deleteMentorship(menteeId, mentorId);
    }

    @Transactional
    public void deleteMentorFromMentees(long mentorId, List<User> mentees) {
        mentees.forEach(mentee -> {
            mentee.getMentors().removeIf(mentor -> mentor.getId() == mentorId);
            mentee.getGoals()
                    .stream()
                    .filter(goal -> goal.getMentor().getId() == mentorId)
                    .forEach(goal -> goal.setMentor(mentee));
        });

        mentorshipRepository.saveAll(mentees);
    }
}
