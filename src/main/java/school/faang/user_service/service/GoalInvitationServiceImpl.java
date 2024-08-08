package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.filters.invitation.InvitationFilter;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.validator.invintation.InvintationDtoValidator;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalInvitationServiceImpl implements GoalInvitationService {
    private final GoalInvitationRepository goalInvitationRepository;
    private final UserRepository userRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private final InvintationDtoValidator invintationDtoValidatorImpl;
    private final List<InvitationFilter> invitationFilters;

    public GoalInvitationDto createInvitation(GoalInvitationDto goalInvitationDto) {
        invintationDtoValidatorImpl.validate(goalInvitationDto);
        GoalInvitation savedInvitation = goalInvitationRepository.save(goalInvitationMapper.toEntity(goalInvitationDto));
        return goalInvitationMapper.toDto(savedInvitation);
    }

    public GoalInvitationDto acceptGoalInvitation(long id) {
        GoalInvitation goalInvitation = goalInvitationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No such goal invitation with id:" + id));

        User invited = goalInvitation.getInvited();
        if (invited.getReceivedGoalInvitations().size() > 3)
            throw new IllegalArgumentException("Exception invited user can`t have more than 3 goal invitations");

        invited.getGoals().add(goalInvitation.getGoal());
        goalInvitation.setStatus(RequestStatus.ACCEPTED);
        userRepository.save(invited);
        goalInvitationRepository.save(goalInvitation);
        return goalInvitationMapper.toDto(goalInvitation);
    }

    public GoalInvitationDto rejectGoalInvitation(long id) {
        GoalInvitation savedInvitation = goalInvitationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No such goal invitation with id:" + id));

        savedInvitation.setStatus(RequestStatus.REJECTED);
        goalInvitationRepository.save(savedInvitation);
        return goalInvitationMapper.toDto(savedInvitation);
    }

    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filters) {
        Stream<GoalInvitation> goalInvitations = goalInvitationRepository.findAll().stream();

        return invitationFilters.stream().filter(filter -> filter.isAcceptable(filters))
                .flatMap(filter -> filter.apply(goalInvitations, filters))
                .map(goalInvitationMapper::toDto)
                .toList();
    }


}
