package school.faang.user_service.service.impl.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.model.dto.mentorship.MentorshipDto;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.mapper.mentorship.MentorshipMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.validator.mentorship.MentorshipValidator;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MentorshipServiceImpl implements MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final MentorshipMapper mentorshipMapper;
    private final MentorshipValidator validator;

    @Override
    public List<MentorshipDto> getMentees(long mentorId) {
        var mentees = new ArrayList<MentorshipDto>();
        mentorshipRepository.findById(mentorId).ifPresent(mentor ->
                mentees.addAll(mentor.getMentees().stream()
                        .map(mentorshipMapper::toDto)
                        .toList())
        );

        return mentees;
    }

    @Override
    public List<MentorshipDto> getMentors(long menteeId) {
        var mentors = new ArrayList<MentorshipDto>();
        mentorshipRepository.findById(menteeId).ifPresent(mentee ->
                mentors.addAll(mentee.getMentors().stream()
                        .map(mentorshipMapper::toDto)
                        .toList())
        );

        return mentors;
    }

    @Override
    @Transactional
    public void deleteMentee(long menteeId, long mentorId) {
        mentorshipRepository.deleteMentorship(menteeId, mentorId);
    }

    @Override
    @Transactional
    public void deleteMentor(long menteeId, long mentorId) {
        mentorshipRepository.deleteMentorship(menteeId, mentorId);
    }

    @Override
    @Transactional
    public void deleteMentorFromMentees(long mentorId, List<User> mentees) {
        validator.validateMenteesList(mentees);

        mentees
                .stream()
                .filter(mentee -> mentee.getMentors() != null)
                .forEach(mentee -> {
                    mentee.getMentors().removeIf(mentor -> mentor.getId() == mentorId);
                    mentee.getGoals()
                            .stream()
                            .filter(goal -> goal.getMentor().getId() == mentorId)
                            .forEach(goal -> goal.setMentor(mentee));
                });

        mentorshipRepository.saveAll(mentees);
    }
}
