package school.faang.user_service.component;

import static school.faang.user_service.exception.ExceptionMessages.DELETION_ERROR_MESSAGE;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.AopInvocationException;
import org.springframework.stereotype.Component;

/**
 * Компонент, который отвечает удаления данных из БД на основе Runnable.
 */
@Slf4j
@Component
public class DeletionDataComponent {

  /**
   * Метод для удаления данных из разных репозиториев.
   * @param deletion функциональный интерфейс, который содержит метод удаления.
   */
  public void deleteData(Runnable deletion) {
    try {
      deletion.run();
    } catch (AopInvocationException e) {
      log.error(DELETION_ERROR_MESSAGE, e);
    }
  }

}
