package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MenteeReadDto;
import school.faang.user_service.mapper.MentorshipMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final MentorshipMapper mentorshipMapper;

    public List<MenteeReadDto> getAllMentees(long userId) {
        var mentees = mentorshipRepository.findMenteesByMentorId(userId);

        return mentees.stream()
                .map(mentorshipMapper::toDto)
                .toList();
    }
}
