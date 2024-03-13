package school.faang.user_service.service;

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

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final MenteeMapper menteeMapper;
    private final MentorMapper mentorMapper;

    public List<User> getMentees(Long userId) {
        User user = getUser(userId);
        if (user == null) {
            return Collections.emptyList();
        }
        return user.getMentees();
    }

    public List<User> getMentors(Long userId) {
        User user = getUser(userId);
        if (user == null) {
            return Collections.emptyList();
        }
        return user.getMentors();
    }

    private User getUser(Long userId) {
        return mentorshipRepository.findById(userId).orElse(null);
    }

    public void deleteMentee(Long menteeId, Long mentorId) {
        User mentor = getMentor(mentorId);
        MentorDTO mentorDTO = mentorMapper.toDTO(mentor);

        if (mentorDTO.getMenteesIds().contains(menteeId)) {
            List<User> listUpdateMentees = removeUserId(mentorDTO.getMenteesIds(), menteeId);
            mentor.setMentees(listUpdateMentees);
            mentorshipRepository.save(mentor);
        } else {
            throw new IllegalArgumentException("The id sent to the mentee id not in the mentee list for this mentor");
        }
    }

    public void deleteMentor(Long menteeId, Long mentorId) {
        User mentee = getMentee(menteeId);
        MenteeDTO menteeDTO = menteeMapper.toDTO(mentee);

        if (menteeDTO.getMentorsIds().contains(mentorId)) {
            List<User> listUpdateMentors = removeUserId(menteeDTO.getMentorsIds(), mentorId);
            mentee.setMentors(listUpdateMentors);
            mentorshipRepository.save(mentee);

        } else {
            throw new IllegalArgumentException("The id sent to the mentor is not in the mentor list for this mentee");
        }
    }

    private List<User> removeUserId(List<Long> userId, Long idToDelete) {
        userId.remove(idToDelete);
        return (List<User>) mentorshipRepository.findAllById(userId);
    }

    private User getMentor(Long mentorId) {
        return mentorshipRepository.findById(mentorId).orElseThrow(() -> new IllegalArgumentException("The sent Mentor_id is invalid"));
    }

    private User getMentee(long menteeId) {
        return mentorshipRepository.findById(menteeId).orElseThrow(() -> new IllegalArgumentException("The sent Mentee_id is invalid"));
    }
}