package school.faang.user_service.service.goal;

import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.dto.filter.GoalInvitationFilterIDto;
import school.faang.user_service.validation.goal.GoalInvitationValidator;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class GoalInvitationService {

    private final GoalInvitationMapper goalInvitationMapper;
    private final List<GoalInvitationFilter> invitationFilters;
    private final GoalInvitationValidator goalInvitationValidator;
    private final GoalInvitationRepository goalInvitationRepository;

    @Autowired
    public GoalInvitationService(GoalInvitationMapper goalInvitationMapper, List<GoalInvitationFilter> invitationFilters,
                                 GoalInvitationValidator goalInvitationValidator, GoalInvitationRepository goalInvitationRepository) {
        this.goalInvitationMapper = goalInvitationMapper;
        this.goalInvitationValidator = goalInvitationValidator;
        this.goalInvitationRepository = goalInvitationRepository;
        this.invitationFilters = Optional.ofNullable(invitationFilters).orElse(List.of());
    }

    @Transactional
    public GoalInvitationDto createInvitation(GoalInvitationDto invitationDto) {
        GoalInvitation goalInvitation = goalInvitationMapper.toEntity(invitationDto);
        goalInvitationValidator.validate(goalInvitation);
        return goalInvitationMapper.toDto(goalInvitationRepository.save(goalInvitation));
    }

    @Transactional
    public GoalInvitationDto acceptGoalInvitation(Long id) {
        GoalInvitation goalInvitation = findInvitation(id);
        goalInvitationValidator.validate(goalInvitation);
        goalInvitationValidator.validateAcceptInvitation(goalInvitation);
        goalInvitation.setStatus(RequestStatus.ACCEPTED);
        goalInvitation.getInvited().getGoals().add(goalInvitation.getGoal());
        return goalInvitationMapper.toDto(goalInvitation);
    }

    @Transactional
    public GoalInvitationDto rejectGoalInvitation(Long id) {
        GoalInvitation goalInvitation = findInvitation(id);
        goalInvitationValidator.validate(goalInvitation);
        goalInvitation.setStatus(RequestStatus.REJECTED);
        return goalInvitationMapper.toDto(goalInvitation);
    }

    @Transactional
    public List<GoalInvitationDto> getInvitations(GoalInvitationFilterIDto filters) {
        Stream<GoalInvitation> invitations = goalInvitationRepository.findAll().stream();

        return invitationFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(invitations, filters))
                        .map(goalInvitationMapper::toDto)
                .toList();
    }

    private GoalInvitation findInvitation(Long id) {
        return goalInvitationRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Invalid request. Invitation with ID: " + id + " does not exist"));
    }
}
