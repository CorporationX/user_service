package school.faang.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.validation.goal.GoalInvitationValidation;
import school.faang.user_service.filter.Filter;

import java.util.List;
import java.util.stream.Stream;


@Service
public class GoalInvitationService {

    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private final GoalInvitationValidation validation;
    private final List<Filter<GoalInvitationDto, InvitationFilterDto>> invitationFilter;
    private final UserService userService;
    private final GoalService goalService;

    @Autowired
    public GoalInvitationService(GoalInvitationRepository goalInvitationRepository,
                                 GoalInvitationMapper goalInvitationMapper,
                                 GoalInvitationValidation goalInvitationValidation,
                                 List<Filter<GoalInvitationDto, InvitationFilterDto>> invitationFilterDto,
                                 UserService userService, GoalService goalService) {
        this.goalInvitationRepository = goalInvitationRepository;
        this.goalInvitationMapper = goalInvitationMapper;
        this.validation = goalInvitationValidation;
        this.invitationFilter = invitationFilterDto;
        this.userService = userService;
        this.goalService = goalService;
    }

    public void createInvitation(GoalInvitationDto dto){
        validation.checkInvitation(dto);
        GoalInvitation goalInvitation = streamAllGoalInvitation()
                .filter(goal -> goal.getId() == dto.getId())
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Goal not found"));

        goalInvitation.setInviter(userService.getUserById(dto.getInviterId()));
        goalInvitation.setInvited(userService.getUserById(dto.getInvitedUserId()));
        goalInvitation.setGoal(goalService.getGoalById(dto.getGoalId()));

        goalInvitation = goalInvitationMapper.toEntity(dto);

        goalInvitationRepository.save(goalInvitation);
    }

    public void acceptGoalInvitation(Long id){
        validation.checkAcceptingInvitation(streamAllGoalInvitationDto(), id);
        decisionOnGoalInvitation(id, RequestStatus.ACCEPTED);
    }

    public void rejectGoalInvitation(Long id){
        validation.checkRejectingInvitation(streamAllGoalInvitationDto(), id);
        decisionOnGoalInvitation(id, RequestStatus.REJECTED);
    }

    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filters){
        Stream<GoalInvitationDto> goalInvitation = streamAllGoalInvitationDto();
        invitationFilter.stream().filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(goalInvitation, filters));
        return goalInvitation.toList();
    }

    private Stream<GoalInvitationDto> streamAllGoalInvitationDto(){
        return goalInvitationRepository.findAll().stream()
                .map(goalInvitationMapper::toDto);
    }

    private Stream<GoalInvitation> streamAllGoalInvitation(){
        return goalInvitationRepository.findAll().stream();
    }

    private void decisionOnGoalInvitation(Long id, RequestStatus status){
        GoalInvitationDto goalInvitationDto = streamAllGoalInvitationDto()
                .filter(filter -> filter.getId().equals(id)).findFirst().orElseThrow();
        goalInvitationDto.setStatus(status);

        GoalInvitation goalInvitation = goalInvitationRepository.findById(id)
                .stream()
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Goal not found"));

        goalInvitationMapper.update(goalInvitation, goalInvitationDto);
        goalInvitationRepository.save(goalInvitation);
    }
}
