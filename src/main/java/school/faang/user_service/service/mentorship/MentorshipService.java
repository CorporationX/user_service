package school.faang.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.mentee.MenteeDTO;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MenteeMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class MentorshipService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MenteeMapper menteeMapper;

    @Autowired
    private UserMapper userMapper;

    public List<MenteeDTO> getMentees(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User not found"));
        return user.getMentees().stream()
                .map(u -> menteeMapper.toDTO(u))
                .collect(Collectors.toList());
    }

    public List<UserDto> getMentors(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User not found"));
        return user.getMentors().stream()
                .map(u -> userMapper.toDto(u))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteMentee(long menteeId, long mentorId) {
        User mentor = userRepository.findById(mentorId).orElseThrow(() -> new NoSuchElementException("Mentor not found"));
        mentor.getMentees().removeIf(mentee -> mentee.getId() == menteeId);
        userRepository.save(mentor);
    }

    @Transactional
    public void deleteMentor(long menteeId, long mentorId) {
        User mentee = userRepository.findById(menteeId).orElseThrow(() -> new NoSuchElementException("Mentee not found"));
        mentee.getMentors().removeIf(mentor -> mentor.getId() == mentorId);
        userRepository.save(mentee);
    }
}