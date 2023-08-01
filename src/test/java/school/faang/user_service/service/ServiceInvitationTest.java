package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.data.redis.connection.RedisGeoCommands.GeoCommandArgs.GeoCommandFlag.any;

@ExtendWith(MockitoExtension.class)

public class ServiceInvitationTest {
    @Mock
    private GoalInvitationRepository goalRepository;
    @InjectMocks
    private GoalInvitationService goalInvitationService;

    @Test
    void CreateGoalInvitation(){
        GoalInvitationDto goalInvitationDto = new GoalInvitationDto();
        goalInvitationDto.setInvitedUserId(2L);
        goalInvitationDto.setInviterId(1L);

        GoalInvitationDto result = goalInvitationService.createInvitation(goalInvitationDto);
        assertNotNull(result);
        assertEquals(2L, result.getInvitedUserId());
        assertEquals(1L, result.getInviterId());

        verify(goalRepository).existsById(goalInvitationDto.getInviterId());
    }

}
