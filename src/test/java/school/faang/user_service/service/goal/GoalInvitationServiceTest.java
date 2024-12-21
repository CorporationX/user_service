package school.faang.user_service.service.goal;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.goal.GoalInvitationMapperImpl;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.validator.UserValidator;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationServiceTest {
    @Mock private UserValidator userValidator;
    @Mock private GoalInvitationRepository goalInvitationRepository;
    @Mock private GoalInvitationMapperImpl goalInvitationMapper;

    @InjectMocks
    private GoalInvitationService service;

    @Test
    public void inviterAndInvitedIdsAreEqualTest() {
        GoalInvitationDto dto = new GoalInvitationDto();
        dto.setInviterId(1L);
        dto.setInvitedUserId(1L);

        Mockito.doThrow(new DataValidationException(""))
                .when(userValidator)
                .areUsersIdEqual(1L, 1L);

        Assert.assertThrows(
                DataValidationException.class,
                () -> service.createInvitation(dto)
        );
    }

    @Test
    public void userDoesNotExistTest() {
        GoalInvitationDto dto = new GoalInvitationDto();
        dto.setInviterId(1L);
        dto.setInvitedUserId(2L);

        Mockito.doThrow(new DataValidationException(""))
                .when(userValidator)
                .areUsersExist(1L, 2L);

        Assert.assertThrows(
                DataValidationException.class,
                () -> service.createInvitation(dto)
        );
    }

    @Test
    public void goalInvitationIsSavedTest() {
        GoalInvitationDto dto = new GoalInvitationDto();
        service.createInvitation(dto);
        Mockito.verify(goalInvitationRepository, Mockito.times(1))
                .save(goalInvitationMapper.toEntity(dto));
    }
}
