package school.faang.user_service.controller.goal;

import com.amazonaws.services.kms.model.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/goal")
public class GoalController {
    private final GoalService goalService;

    @GetMapping("/get/{userId}")
    public ResponseEntity<List<GoalDto>> getGoalsByUser(@PathVariable Long userId,
                                                        @RequestBody(required = false) GoalFilterDto filter) {
        try {
            return ResponseEntity.ok(goalService.getGoalsByUser(userId, filter));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
