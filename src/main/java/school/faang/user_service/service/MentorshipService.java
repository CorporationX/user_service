package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class MentorshipService {

  private final MentorshipRepository mentorshipRepository;
  private final UserMapper userMapper;

  public List<UserDto> getMentees(long mentorId) {
    return getUsers(mentorId, User::getMentees);
  }

  public List<UserDto> getMentors(long menteeId) {
    return getUsers(menteeId, User::getMentors);
  }

  public Optional<UserDto> deleteMentee(long mentorId, long menteeId) {
    return processRelationship(mentorId, menteeId,
        (mentor, mentee) -> userMapper.toDto(mentee),
        (mentor, mentee) -> mentor.getMentees().remove(mentee));
  }

  public Optional<UserDto> deleteMentor(long menteeId, long mentorId) {
    return processRelationship(menteeId, mentorId,
        (mentee, mentor) -> userMapper.toDto(mentor),
        (mentee, mentor) -> mentee.getMentors().remove(mentor));
  }

  private List<UserDto> getUsers(long userId, Function<User, List<User>> getUsersFunction) {
    return mentorshipRepository.findById(userId)
        .map(getUsersFunction)
        .orElseGet(Collections::emptyList)
        .stream()
        .map(userMapper::toDto)
        .toList();
  }

  private <T> Optional<T> processRelationship(long userId1, long userId2, BiFunction<User, User, T> action,
      BiConsumer<User, User> modifyRelationship) {
    Optional<User> user1 = mentorshipRepository.findById(userId1);
    Optional<User> user2 = mentorshipRepository.findById(userId2);

    if (user1.isPresent() && user2.isPresent()) {
      modifyRelationship.accept(user1.get(), user2.get());
      mentorshipRepository.save(user1.get());
      return Optional.of(action.apply(user1.get(), user2.get()));
    }

    return Optional.empty();
  }

}