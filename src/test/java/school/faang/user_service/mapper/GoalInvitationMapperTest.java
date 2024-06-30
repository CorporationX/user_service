package school.faang.user_service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationMapperTest {

    private final GoalInvitationMapper goalInvitationMapper = Mappers.getMapper(GoalInvitationMapper.class);

    private GoalInvitation goalInvitation;

    private GoalInvitationDto goalInvitationDto;

    @BeforeEach
    void setUp() {
        goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);

        goalInvitationDto = new GoalInvitationDto();
        goalInvitationDto.setId(1L);
    }

    @Test
    void testToEntity() {
        GoalInvitation actualGoalInvitation = goalInvitationMapper.toEntity(goalInvitationDto);
        actualGoalInvitation.setId(1);
        assertEquals(goalInvitation, actualGoalInvitation);
    }

    @Test
    void testToDto() {
        GoalInvitationDto actualGoalInvitationDto = goalInvitationMapper.toDto(goalInvitation);
        assertEquals(goalInvitationDto, actualGoalInvitationDto);
    }
}
