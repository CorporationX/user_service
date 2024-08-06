package school.faang.user_service.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.AopInvocationException;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipDto;
import school.faang.user_service.mapper.MentorshipMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

/**
 * Класс-сервис, который отвечает за бизнес-логику управления наставничеством.
 */
@Service
@RequiredArgsConstructor
public class MentorshipService {

  private static final int MIN_VALUE_DELETED_ROWS = 0;
  private static final String DELETION_ERROR_MESSAGE = "Произошла ошибка при удалении записи.";
  private static final Logger LOG = LoggerFactory.getLogger(MentorshipService.class);

  private final MentorshipRepository mentorshipRepository;
  private final MentorshipMapper mentorshipMapper;

  /**
   * Метод для получения всех менти одного пользователя.
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
      LOG.error(DELETION_ERROR_MESSAGE, e);
    }
    return result > MIN_VALUE_DELETED_ROWS ? Boolean.TRUE : Boolean.FALSE;
  }

}
