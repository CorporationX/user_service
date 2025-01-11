package school.faang.user_service.service;

import school.faang.user_service.dto.request.CreateGoalRequestDto;
import school.faang.user_service.dto.request.SearchRequest;
import school.faang.user_service.dto.response.CreateGoalResponseDto;
import school.faang.user_service.dto.response.GoalDto;

import java.util.List;

public interface GoalService {

    CreateGoalResponseDto createGoal(Long userId, CreateGoalRequestDto request);

    void deleteGoal(Long goalId);

    List<GoalDto> findSubtasksByGoalId(Long goalId);

    List<GoalDto> search(SearchRequest request);

}
