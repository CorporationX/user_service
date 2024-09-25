package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MentorshipService {

    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    public List<UserDto> getMentees(long mentorId) {
        return mentorshipRepository.findById(mentorId)
                .map(mentor -> getUsers(mentor, User::getMentees))
                .orElseThrow(() -> new EntityNotFoundException("Ментор с ID " + mentorId + " не найден"));
    }

    public List<UserDto> getMentors(long menteeId) {
        return mentorshipRepository.findById(menteeId)
                .map((User mentee) -> getUsers(mentee, User::getMentors))
                .orElseThrow(() -> new EntityNotFoundException("Менти с ID " + menteeId + " не найден"));
    }

    public void deleteMentee(long mentorId, long menteeId) {
        breakRelationship(mentorId, menteeId,
                (mentor, mentee) -> mentor.getMentees().remove(mentee));
    }

    public void deleteMentor(long menteeId, long mentorId) {
        breakRelationship(menteeId, mentorId,
                (mentee, mentor) -> mentee.getMentors().remove(mentor));
    }

    private List<UserDto> getUsers(User user, Function<User, List<User>> relationExtractor) {
        return relationExtractor.apply(user).stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    private void breakRelationship(long userId1, long userId2, BiConsumer<User, User> modifyRelationship) {
        User user1 = mentorshipRepository.findById(userId1)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + userId1 + " не найден"));
        User user2 = mentorshipRepository.findById(userId2)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + userId2 + " не найден"));

        modifyRelationship.accept(user1, user2);
        mentorshipRepository.save(user1);
    }

}