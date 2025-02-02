package school.faang.user_service.controller.goal;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalDTO;
import school.faang.user_service.dto.goal.GoalFilterDTO;
import school.faang.user_service.exception.BadRequestException;
import school.faang.user_service.service.goal.GoalService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/goals")
public class GoalController {

  private final GoalService goalService;

  @PostMapping
  public GoalDTO createGoal(@RequestParam Long userId, @RequestBody GoalDTO goalDTO) {
    if (goalDTO.getTitle() == null || goalDTO.getTitle().isBlank()) {
      throw new BadRequestException("Title can not be empty");
    }
    return goalService.createGoal(userId, goalDTO);
  }

  @PutMapping("/{goalId}")
  public GoalDTO updateGoal(@PathVariable Long goalId, @RequestBody GoalDTO goalDTO) {
    if (goalDTO.getTitle() == null || goalDTO.getTitle().isBlank()) {
      throw new BadRequestException("Title can not be empty");
    }
    return goalService.updateGoal(goalId, goalDTO);
  }

  @GetMapping("user/{userId}")
  public List<GoalDTO> getGoalsByUser(
      @PathVariable Long userId,
      @RequestParam(required = false) String title,
      @RequestParam(required = false) String status) {
    GoalFilterDTO goalFilterDTO = new GoalFilterDTO(title, status);
    return goalService.getGoalsByUser(userId, goalFilterDTO);
  }

  @GetMapping("sub-goals/{parentId}")
  public List<GoalDTO> findSubGoals(
      @PathVariable Long parentId,
      @RequestParam(required = false) String title,
      @RequestParam(required = false) String status) {
    GoalFilterDTO goalFilterDTO = new GoalFilterDTO(title, status);
    return goalService.getSubGoals(parentId, goalFilterDTO);
  }

  @DeleteMapping("/{id}")
  public void deleteGoal(@PathVariable Long id) {
    goalService.deleteGoal(id);
  }
}
