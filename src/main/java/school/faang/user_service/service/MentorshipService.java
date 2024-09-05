package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.MentorshipDto;
import school.faang.user_service.mapper.MentorshipMapper;
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
        var mentees = mentorshipRepository.findMenteesByMentorId(mentorId);

        return mentees.stream()
                .map(mentorshipMapper::toDto)
                .toList();
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
        mentorshipRepository.deleteMentorsMenteeByIds(menteeId, mentorId);
    }
}
