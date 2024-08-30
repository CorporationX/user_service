package school.faang.user_service.controller.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.userPremium.UserFilterDto;
import school.faang.user_service.dto.userPremium.UserPremiumDto;
import school.faang.user_service.service.user.DeactivateUserFacade;
import school.faang.user_service.service.userPremium.UserPremiumService;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  private static final long DEFAULT_ID = 1L;

  private ResponseEntity responseEntity;

  @InjectMocks
  private UserPremiumController userPremiumController;
  @Mock
  private UserPremiumService userPremiumService;

  @InjectMocks
  private UserController userController;
  @Mock
  private DeactivateUserFacade userService;

  private UserDto createUserDto() {
    return UserDto.builder()
        .id(2L)
        .username("JaneSmith")
        .email("janesmith@example.com")
        .phone("0987654321")
        .aboutMe("About Jane Smith")
        .city("London")
        .active(false)
        .premium("")
        .build();
  }

  @Test
  void testGetListPremiumUsersNull() {
    assertThrows(IllegalArgumentException.class,
        () -> userPremiumController.getListPremiumUsers(null));
  }

  @Test
  void testGetListPremiumUsersTrue() {
    Mockito.when(userPremiumService.getPremiumUsers(Mockito.any()))
        .thenReturn(List.of(new UserPremiumDto()));

    userPremiumController.getListPremiumUsers(new UserFilterDto());

    Mockito.verify(userPremiumService, Mockito.times(1)).getPremiumUsers(new UserFilterDto());
  }

  @Test
  @DisplayName("Проверка ответа с статусом 200, типом сообщения и тела сообщения если пользователь был деактивирован.")
  void testGetMenteesReturnsValidResponseEntity() {
    when(userService.deactivateUser(DEFAULT_ID)).thenReturn(createUserDto());
    responseEntity = userController.deactivateUser(DEFAULT_ID);
    assertNotNull(responseEntity);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
    assertEquals(createUserDto(), responseEntity.getBody());
  }

}