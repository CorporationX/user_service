package school.faang.user_service.controller.goal;

import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/goal")
public class GoalController {
    private final GoalService goalService;

    @PostMapping
    public GoalDto createGoal(@RequestBody GoalDto goalDto, Long userId){

        return goalService.createGoal(goalDto, userId);
    }

    @PutMapping
    public GoalDto updateGoal(@RequestBody GoalDto goalDto, Long userId){

        return goalService.updateGoal(goalDto, userId);
    }
}