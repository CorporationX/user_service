package school.faang.user_service.controller.skill;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.dto.skill.SkillCandidateDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/skills")
@RequiredArgsConstructor
@Tag(name = "Skill Controller")
public class SkillController {
    private final SkillService skillService;

    @Operation(summary = "Acquire skill from offers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully acquired skill",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SkillDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SkillDto.class)))
    })
    @GetMapping("/{skillId}/users/{userId}")
    public SkillDto acquireSkillFromOffers(@PathVariable long skillId, @PathVariable long userId) {
        log.info("Received request to acquire skill with ID {} for user ID {}", skillId, userId);
        return skillService.acquireSkillFromOffers(skillId, userId);
    }

    @Operation(summary = "Get offered skills for user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved offered skills",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SkillCandidateDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SkillCandidateDto.class)))
    })
    @GetMapping("/user/{userId}/offered-skills")
    public List<SkillCandidateDto> getOfferedSkills(@PathVariable long userId) {
        log.info("Received request to get offered skills for user ID {}", userId);
        return skillService.getOfferedSkills(userId);
    }

    @Operation(summary = "Get skills for user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user skills",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SkillDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/user/{userId}")
    public List<SkillDto> getUserSkills(@PathVariable long userId) {
        log.info("Received request to get user skills for user ID {}", userId);
        return skillService.getUserSkills(userId);
    }

    @Operation(summary = "Create a new skill")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created skill",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SkillDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/create")
    public SkillDto create(@RequestBody SkillDto skill) {
        log.info("Received request to create skill with title '{}'", skill.getTitle());
        validateSkill(skill);
        return skillService.create(skill);
    }

    @Operation(summary = "Get all skills")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all skills",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SkillDto.class)))
    })
    @GetMapping("/all")
    public List<SkillDto> getAllSkills() {
        log.info("Received request to get all skills");
        return skillService.getAllSkills();
    }

    private void validateSkill(SkillDto skill) {
        if (skill.getTitle() == null || skill.getTitle().trim().isEmpty()) {
            log.warn("Validation failed: Skill title cannot be empty");
            throw new DataValidationException("Skill title cannot be empty");
        }
    }
}
