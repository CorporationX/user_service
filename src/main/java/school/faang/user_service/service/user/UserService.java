package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.repository.DeactivateUserFacade;

/**
 * Класс-сервис, который отвечает за бизнес-логику управления деактивации пользователя.
 */
@Service
@RequiredArgsConstructor
public class UserService {

  private final DeactivateUserFacade deactivateUserFacade;

  public UserDto deactivateUser(long userId) {
    return deactivateUserFacade.deactivateUser(userId);
  }

}
