package school.faang.user_service.mapper.mymappers;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import school.faang.user_service.dto.mydto.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.tasksEntity.notFoundExceptions.SkillNotFoundException;
import school.faang.user_service.exception.tasksEntity.notFoundExceptions.contact.UserNotFoundException;
import school.faang.user_service.exception.tasksEntity.notFoundExceptions.goal.GoalNotFoundException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class Goal1Mapper {

    @Autowired
    protected SkillRepository skillRepository;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected GoalRepository goalRepository;

    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "mentor.id", target = "mentorId")
    @Mapping(source = "skillsToAchieve", target = "skillIds", qualifiedByName = "mapSkillsToIdList")
    public abstract GoalDto toDto(Goal goal);

    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "mentor", ignore = true)
    @Mapping(target = "skillsToAchieve", ignore = true)
    public abstract Goal toEntity(GoalDto goalDto);

    public abstract void update(GoalDto goalDto, @MappingTarget Goal goal);

    @Named("mapSkillsToIdList")
    protected List<Long> mapSkillsToIdList(List<Skill> skillsToAchieve) {
        if (skillsToAchieve == null)
            return Collections.emptyList();
        return skillsToAchieve.stream().map(Skill::getId).toList();
    }

    public void convertDtoDependenciesToEntity(GoalDto goalDto, Goal goal) {
        if (goalDto.getMentorId() != null) {
            User mentor = userRepository.findById(goalDto.getMentorId())
                    .orElseThrow(() -> new UserNotFoundException("Mentor with given id was not found!"));
            goal.setMentor(mentor);
        }

        if (goalDto.getParentId() != null) {
            Goal goalParent = goalRepository.findById(goalDto.getParentId())
                    .orElseThrow(() -> new GoalNotFoundException("Goal-parent with given id was not found!"));
            goal.setParent(goalParent);
        }

        if (goalDto.getSkillIds() != null) {
            List<Skill> skills = new ArrayList<>();
            goalDto.getSkillIds().forEach(skillId -> {
                Skill skill = skillRepository.findById(skillId)
                        .orElseThrow(() -> new SkillNotFoundException("There is no way to add a goal with a non-existent skill!"));
                skills.add(skill);
            });
            goal.setSkillsToAchieve(skills);
        }
    }
}
