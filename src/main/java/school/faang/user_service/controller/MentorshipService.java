package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final MenteeMapper menteeMapper;

    public List<MenteeDto> getMentees(long userId) {
        if (userId < 1) {
            throw new RuntimeException("Invalid Argument");
        }
        return mentorshipRepository.findById(userId).stream().map(menteeMapper::toUser).toList();
    }
}
