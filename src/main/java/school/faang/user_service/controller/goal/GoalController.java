package school.faang.user_service.controller.goal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.FilterGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.service.goal.GoalService;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/goals")
@Tag(name = "Goals", description = "Operations for managing goals")
public class GoalController {
    private final GoalService goalService;

    @Operation(
            summary = "List goals",
            description = "Retrieves a paginated list of goals based on given filters"
    )
    @GetMapping
    public ResponseEntity<Page<GoalDto>> getGoals(
            @ParameterObject FilterGoalDto dto,
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity
                .ok()
                .body(goalService.get(dto, pageable));
    }

    @Operation(
            summary = "Create a goal",
            description = "Creates a new goal"
    )
    @PostMapping
    public ResponseEntity<GoalDto> create(
            @RequestBody @Valid CreateGoalDto createGoalDto

    ) {
        GoalDto result = goalService.create(createGoalDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(result);
    }

    @Operation(
            summary = "Update an existing goal",
            description = "Updates the details of a goal by its identifier",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Goal successfully updated"),
                    @ApiResponse(responseCode = "422", description = "Invalid data provided"),
                    @ApiResponse(responseCode = "404", description = "Goal not found")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<GoalDto> update(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateGoalDto updateGoalDto
    ) {
        GoalDto result = goalService.update(id, updateGoalDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    @Operation(
            summary = "Delete a goal",
            description = "Deletes a goal by its identifier"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable("id") Long id
    ) {
        goalService.delete(id);
        return ResponseEntity
                .ok()
                .build();
    }
}
