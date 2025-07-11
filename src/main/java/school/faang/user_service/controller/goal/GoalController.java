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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
@Tag(name = "Цели", description = "Взаимодействие с целями")
public class GoalController {
    private final GoalService goalService;

    @Operation(
            summary = "Список целей",
            description = "Позволяет получить список отфильтрованных целей"
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
            summary = "Создать цель",
            description = "Позволяет создать цель"
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
            summary = "Обновить существующую цель",
            description = "Обновляет данные цели по идентификатору",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Цель успешно обновлена"),
                    @ApiResponse(responseCode = "400", description = "Невалидные данные цели"),
                    @ApiResponse(responseCode = "404", description = "Цель не найдена")
            }
    )
    @PatchMapping("/{id}")
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
            summary = "Удалить цель",
            description = "Позволяет удалить цель"
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
