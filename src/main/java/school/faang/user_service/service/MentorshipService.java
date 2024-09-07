package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MentorshipService {

    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    private <T> Optional<T> processMenteeOrMentor(long mentorId, long menteeId, BiFunction<User, User, T> action) {
        Optional<User> mentor = mentorshipRepository.findById(mentorId);
        Optional<User> mentee = mentorshipRepository.findById(menteeId);
    
        if (mentor.isPresent() && mentee.isPresent()) {
            return Optional.of(action.apply(mentor.get(), mentee.get()));
        }
    
        return Optional.empty(); 
    }
    

    public List<UserDto> getMentees(long mentorId) {
        return mentorshipRepository.findById(mentorId)
            .map(User::getMentees)
            .orElseGet(Collections::emptyList)
            .stream()
            .map(userMapper::toDto)
            .collect(Collectors.toList());
    }

    public List<UserDto> getMentors(long menteeId) {
        return mentorshipRepository.findById(menteeId)
            .map(User::getMentors)
            .orElseGet(Collections::emptyList)
            .stream()
            .map(userMapper::toDto)
            .collect(Collectors.toList());
    }

    public Optional<UserDto> deleteMentee(long mentorId, long menteeId) {
        return processMenteeOrMentor(mentorId, menteeId, (mentor, mentee) -> {
            mentor.getMentees().remove(mentee);
            mentorshipRepository.save(mentor);
            return userMapper.toDto(mentee);
        });
    }
    

    public Optional<UserDto> deleteMentor(long menteeId, long mentorId) {
        return processMenteeOrMentor(menteeId, mentorId, (mentee, mentor) -> {
            mentee.getMentors().remove(mentor);
            mentorshipRepository.save(mentee);
            return userMapper.toDto(mentor);
        });
    }
    
}
