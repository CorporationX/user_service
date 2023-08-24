package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.GoalInvitationException;
import school.faang.user_service.filters.filtersForGoalInvitation.GoalInvitationFilter;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalInvitationService {
    private final GoalInvitationRepository goalInvitationRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private final List<GoalInvitationFilter> goalInvitationFilters;
    private static final int maxGoals = 3;


    public GoalInvitationDto createInvitation(GoalInvitationDto invitationDto) {

        UserDto inviter = UserMapper.INSTANCE.userToDto(userRepository.findById(invitationDto.getInviterId()).get());
        UserDto invited = UserMapper.INSTANCE.userToDto(userRepository.findById(invitationDto.getInvitedUserId()).get());
        if (invitationDto.getInvitedUserId() <= 0 && invitationDto.getInviterId() <= 0) {
            throw new GoalInvitationException("InvationDto not found");
        }
        if (!userRepository.existsById(invitationDto.getInviterId())
                && !userRepository.existsById(invitationDto.getInvitedUserId())) {
            throw new GoalInvitationException("InvationDto missing from the database");
        }
        Optional<Goal> goal = goalRepository.findById(invitationDto.getGoalId());
        //GoalInvitation goalInvitation = goalInvitationMapper.toEntity(invitationDto);
        GoalInvitation savedGoalInvitation = goalInvitationRepository.save(new GoalInvitation(
                invitationDto.getId(),
                goal.get(),
                UserMapper.INSTANCE.dtoToUser(inviter),
                UserMapper.INSTANCE.dtoToUser(invited),
                invitationDto.getStatus(),
                LocalDateTime.now(),
                LocalDateTime.now()));

      //  return goalInvitationMapper.toDto(goalInvitationRepository.save(goalInvitation));
        return goalInvitationMapper.toDto(savedGoalInvitation);
    }


    public GoalInvitationDto acceptGoalInvitation(long id) {

        long idUser = goalInvitationRepository.findById(id).get().getInvited().getId();

        boolean haveTask = userRepository.findById(idUser).get().getGoals().contains(goalRepository.findById(id).get());

        boolean sizeUserGoals = userRepository.findById(idUser).get().getGoals().size() >= maxGoals;

        if (!haveTask || sizeUserGoals) {
            throw new GoalInvitationException("The user is already in the goal or he is already participating in three goals");
        }

        goalInvitationRepository.findById(id).get().setStatus(RequestStatus.valueOf("ACCEPTED"));

        GoalInvitationDto invitationDto = goalInvitationMapper.toDto((goalInvitationRepository.findById(id).stream().findAny().get()));

        return invitationDto;
    }

    public GoalInvitationDto rejectGoalInvitation(long id) {
        if (!goalRepository.existsById(id)) {
            throw new GoalInvitationException("target not found in database");
        }
        goalInvitationRepository.findById(id).get().setStatus(RequestStatus.valueOf("REJECTED"));

        GoalInvitationDto invitationDto = goalInvitationMapper.toDto
                ((goalInvitationRepository.findById(id)
                        .stream()
                        .findAny()
                        .get()));
        return invitationDto;
    }

    private List<GoalInvitationDto> apply(Stream<GoalInvitation> goalInvitation, InvitationFilterDto filters) {
        return goalInvitationFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(goalInvitation, filters))
                .map(goalInvitationMapper::toDto)
                .toList();
    }

    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filters) {
        List<GoalInvitation> invitations = (List<GoalInvitation>) goalInvitationRepository.findAll();

        if (invitations.isEmpty()) {
            return new ArrayList<>();
        }
       return apply(invitations.stream(),filters).stream().toList();

    }

}
