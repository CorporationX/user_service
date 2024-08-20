package school.faang.user_service.controller.mentorship;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.controller.ApiPath;
import school.faang.user_service.dto.MentorshipDto;
import school.faang.user_service.service.mentorship.MentorshipService;

/**
 * Контроллер отвечающий за обработку запросов пользователя для управления наставничеством.
 */
@RestController
@RequestMapping(ApiPath.MENTORSHIP)
@RequiredArgsConstructor
public class MentorshipController {

  private static final String RECORD_DELETED = "Запись была удалена!";

  private final MentorshipService mentorshipService;

  @GetMapping("/mentees/{id}")
  public ResponseEntity<List<MentorshipDto>> getMentees(@PathVariable("id") long id) {
    return responseEntityForMentorshipList(mentorshipService.getMentees(id));
  }

  @GetMapping("/mentors/{id}")
  public ResponseEntity<List<MentorshipDto>> getMentors(@PathVariable("id") long id) {
    return responseEntityForMentorshipList(mentorshipService.getMentors(id));
  }

  @DeleteMapping("/mentee/{menteeId}/mentor/{mentorId}")
  public ResponseEntity<String> deleteMentee(@PathVariable("menteeId") long menteeId,
      @PathVariable("mentorId") long mentorId) {
    return responseEntityOnDelete(mentorshipService.getDeletionResultMentorship(menteeId, mentorId));
  }

  @DeleteMapping("/mentor/{mentorId}/mentee/{menteeId}")
  public ResponseEntity<String> deleteMentor(@PathVariable("mentorId") long mentorId,
      @PathVariable("menteeId") long menteeId) {
    return responseEntityOnDelete(mentorshipService.getDeletionResultMentorship(menteeId, mentorId));
  }

  /**
   * Метод для ответа по запросу в зависимости от полученного списка пользователей.
   * @param lists Список пользователей, которые были получены при запросе.
   * @return ответ на запрос. Статусы 200 или 404.
   */
  private ResponseEntity<List<MentorshipDto>> responseEntityForMentorshipList(List<MentorshipDto> lists) {
    return CollectionUtils.isEmpty(lists)
        ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
        : ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(lists);
  }

  /**
   * Метод для ответа по запросу в зависимости от удачного удалении записи.
   * @param result результат удаления записи.
   * @return ответ на запрос. Статусы 200 или 404.
   */
  private ResponseEntity<String> responseEntityOnDelete(boolean result) {
    return Boolean.TRUE.equals(result)
        ? ResponseEntity.ok()
        .contentType(MediaType.valueOf(MediaType.TEXT_HTML_VALUE))
        .body(RECORD_DELETED)
        : new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

}
