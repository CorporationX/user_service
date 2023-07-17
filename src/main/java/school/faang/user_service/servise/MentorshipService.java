package school.faang.user_service.servise;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MapperUserDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserRepository userRepository;
    private final MapperUserDto mapperUserDto;

    public List<UserDto> getMentees(Long userId) {
        if (mentorshipRepository.findById(userId).isPresent()) {
            return mapperUserDto.toDto(mentorshipRepository.findById(userId).get().getMentees());
        } else {
            throw new EntityNotFoundException("Invalid mentee ID");
        }
    }

    public List<UserDto> getMentors(Long userId) {
        if (mentorshipRepository.findById(userId).isPresent()) {
            return mapperUserDto.toDto(mentorshipRepository.findById(userId).get().getMentors());
        } else {
            throw new EntityNotFoundException("Invalid mentor ID");
        }
    }

    public void deleteMentee(Long menteeId, Long mentorId) {
        User mentee = validate(menteeId);
        User mentor = validate(mentorId);

        if (mentor.getId() == mentee.getId()) {
            throw new IllegalArgumentException("Invalid deletion. You can't be mentee of yourself");
        }

        mentor.getMentees().remove(mentee);
        userRepository.save(mentor);
    }

    public void deleteMentor(Long menteeId, Long mentorId) {
        User mentee = validate(menteeId);
        User mentor = validate(mentorId);

        if (mentor.getId() == mentee.getId()) {
            throw new IllegalArgumentException("Invalid deletion. You can't be mentor of yourself");
        }

        mentee.getMentors().remove(mentor);
        userRepository.save(mentee);
    }

    private User validate(Long Id) {
        return userRepository.findById(Id).orElseThrow(() ->
                new EntityNotFoundException("Invalid ID"));
    }
}


