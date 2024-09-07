package school.faang.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MentorshipService {

    @Autowired
    private MentorshipRepository mentorshipRepository;

    @Autowired
    private MenteeMapper menteeMapper;

    public List<MenteeDTO> getMentees(long userId) {
        List<Mentee> mentees = mentorshipRepository.findMenteesByUserId(userId);
        return mentees.stream()
                .map(menteeMapper::menteeToMenteeDTO)
                .collect(Collectors.toList());
    }
}