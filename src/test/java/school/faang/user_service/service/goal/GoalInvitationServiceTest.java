package school.faang.user_service.service.goal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.component.DeletionDataComponent;
import school.faang.user_service.repository.goal.GoalRepository;

@ExtendWith(MockitoExtension.class)
class GoalInvitationServiceTest {

  @Mock
  private GoalRepository goalRepository;

  @Mock
  private DeletionDataComponent deletionDataComponent;

  @InjectMocks
  private GoalInvitationService goalInvitationService;

  @Test
  @DisplayName("Проверка выброса исключения при удалении отправленных или полученных целей пользователя.")
  public void testDeleteGoalInvitations() {
    when(goalRepository.deleteAllGoalInvitationById(1L)).thenThrow();
    assertThrows(Exception.class, () -> goalInvitationService.deleteGoalInvitations(1L));
  }

}