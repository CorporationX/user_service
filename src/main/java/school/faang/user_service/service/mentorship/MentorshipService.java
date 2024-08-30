package school.faang.user_service.service.mentorship;

import static school.faang.user_service.exception.ExceptionMessages.DELETION_ERROR_MESSAGE;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.AopInvocationException;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

/**
 * Класс-сервис, который отвечает за бизнес-логику управления наставничеством.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipService {

  private static final int MIN_VALUE_DELETED_ROWS = 0;
  private static final String MESSAGE_ABOUT_REMOVE_MENTOR_FROM_MENTEE_LIST
      = "Пользователь был удален из целей, которые он создал.";

  private final MentorshipRepository mentorshipRepository;
  private final MentorshipMapper mentorshipMapper;

  /**
   * Метод для получения всех менти одного пользователя.
   *
   * @param userId id пользователя.
   * @return список всех доступных менти для пользователя.
   */
  public List<MentorshipDto> getMentees(long userId) {
    return mentorshipRepository.getAllMenteesById(userId).stream()
        .map(mentorshipMapper::mentorshipDtoFromUser)
        .toList();
  }

  /**
   * Метод для получения всех менторов одного пользователя
   *
   * @param userId id пользователя.
   * @return список всех доступных менторов для пользователя.
   */
  public List<MentorshipDto> getMentors(long userId) {
    return mentorshipRepository.getAllMentorsById(userId).stream()
        .map(mentorshipMapper::mentorshipDtoFromUser)
        .toList();
  }

  /**
   * Метод для удаления записи () из mentorship.
   * Удаление менти ментором или удаления ментора из списка менторов конкретного пользователя.
   * @param menteeId id ментора
   * @param mentorId id менти
   */
  public boolean getDeletionResultMentorship(long menteeId, long mentorId) {
    int result = MIN_VALUE_DELETED_ROWS;
    try {
      result = mentorshipRepository.deleteMentorshipById(
          mentorshipRepository.getMentorshipIdByMentorIdAndMenteeId(mentorId, menteeId));
    } catch (AopInvocationException e) {
      log.error(DELETION_ERROR_MESSAGE, e);
    }
    return result > MIN_VALUE_DELETED_ROWS ? Boolean.TRUE : Boolean.FALSE;
  }

  /**
   * Метод для удаления пользователя из списка менти, где он был ментором.
   * @param user пользователь, чей аккаунт деактивируется.
   */
  public void removeMentorFromMenteeList(User user) {
    Optional.ofNullable(user.getMentees())
        .ifPresent(mentee -> {
          mentee.forEach(currentMentee -> currentMentee.getMentors().remove(user));
          log.info(MESSAGE_ABOUT_REMOVE_MENTOR_FROM_MENTEE_LIST);
        });
  }

}
