package school.faang.user_service.mapper.goal;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.GoalInvitation;

@ExtendWith(MockitoExtension.class)
class GoalInvitationMapperTest {

    private GoalInvitationMapper mapper = Mappers.getMapper(GoalInvitationMapper.class);

    @Test
    void test() {
        GoalInvitationDto goalInvitationDto = new GoalInvitationDto();
        goalInvitationDto.setInviterId(1L);
        goalInvitationDto.setInvitedUserId(2L);
        goalInvitationDto.setGoalId(1L);
        goalInvitationDto.setStatus(RequestStatus.ACCEPTED);

        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation = mapper.toEntity(goalInvitationDto);
        System.out.println(goalInvitation);


        GoalInvitationDto goalInvitationDto1 = mapper.toDto(goalInvitation);
        System.out.println(goalInvitationDto1);
    }
}