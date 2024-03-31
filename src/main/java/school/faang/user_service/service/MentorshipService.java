package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MenteeDTO;
import school.faang.user_service.dto.MentorDTO;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MenteeMapper;
import school.faang.user_service.mapper.MentorMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final MenteeMapper menteeMapper;
    private final MentorMapper mentorMapper;

    public List<User> getMentees(Long userId) {
        User user = getUser(userId);
        if (user.getMentees()==null) {
            return Collections.emptyList();
        }
        return user.getMentees();
    }

    public List<User> getMentors(Long userId) {
        User user = getUser(userId);
        if (user.getMentors()==null) {
            return Collections.emptyList();
        }
        return user.getMentors();
    }

    private User getUser(Long userId) {
        return mentorshipRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("The sent User_id " + userId + " not found in Database"));
    }

    public void deleteMentee(Long menteeId, Long mentorId) {
        User mentor = getMentor(mentorId);
        User mentee = getMentee(menteeId);

        List<User> mentees = mentor.getMentees();

        if (mentees.contains(mentee)) {
            mentor.setMentees(mentees.
                    stream().
                    filter(user -> !user.equals(mentee)).
                    collect(Collectors.toList()));
            mentorshipRepository.save(mentor);
        } else {
            throw new IllegalArgumentException("The id: " + menteeId + " sent to the mentee id not in the mentee list for this mentor");
        }
    }

    public void deleteMentor(Long menteeId, Long mentorId) {
        User mentee = getMentee(menteeId);
        User mentor = getMentor(mentorId);
        List<User> mentors = mentee.getMentors();
        if (mentors.contains(mentor)) {
            mentee.setMentors(mentors.
                    stream().
                    filter(user -> !user.equals(mentor)).
                    collect(Collectors.toList()));
            mentorshipRepository.save(mentee);
        } else {
            throw new IllegalArgumentException("The id: " + mentorId + "sent to the mentor is not in the mentor list for this mentee");
        }
    }

    private User getMentor(Long mentorId) {
        return mentorshipRepository.findById(mentorId).orElseThrow(() -> new EntityNotFoundException("The sent Mentor_id " + mentorId + " not found in Database"));
    }

    private User getMentee(long menteeId) {
        return mentorshipRepository.findById(menteeId).orElseThrow(() -> new EntityNotFoundException("The sent Mentee_id " + menteeId + " not found in Database"));
    }

    public void deleteMentorForHisMentees(Long mentorId, List<User> mentees){
        mentees.stream().forEach(mentee -> {
            mentee.getMentors().removeIf(mentor -> mentor.getId() == mentorId);
            mentee.getGoals().stream()
                    .filter(goal -> goal.getMentor().getId() == mentorId)
                    .forEach(goal -> goal.setMentor(mentee));
        });
        mentorshipRepository.saveAll(mentees);
    }
}