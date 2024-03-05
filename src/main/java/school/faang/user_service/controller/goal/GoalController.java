package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.service.goal.GoalService;

@RestController()
@RequestMapping("/goal")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @PostMapping("/{id}")
    public ResponseEntity<GoalDto> createGoal(@PathVariable Long id, @RequestBody GoalDto goalDto) {
        return new ResponseEntity<>(goalService.createGoal(id, goalDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public GoalDto updateGoal(@PathVariable Long id, @RequestBody GoalDto goalDto) {
        return goalService.updateGoal(id, goalDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteGoal(@PathVariable Long id) {
        goalService.deleteGoal(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}