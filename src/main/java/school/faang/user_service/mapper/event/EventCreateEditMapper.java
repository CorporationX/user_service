package school.faang.user_service.mapper.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import school.faang.user_service.dto.event.EventCreateEditDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {UserRepository.class})
public abstract class EventCreateEditMapper {

    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected SkillRepository skillRepository;

    @Mappings({
            @Mapping(target = "owner", expression = "java(findUserById(eventCreateEditDto.getOwnerId()))"),
            @Mapping(target = "relatedSkills", expression = "java(findSkillsByIds(eventCreateEditDto.getRelatedSkillIds()))")
    })
    public abstract Event map(EventCreateEditDto eventCreateEditDto);

    @Mappings({
            @Mapping(target = "owner", expression = "java(findUserById(eventCreateEditDto.getOwnerId()))"),
            @Mapping(target = "relatedSkills", expression = "java(findSkillsByIds(eventCreateEditDto.getRelatedSkillIds()))")
    })
    public abstract Event map(EventCreateEditDto eventCreateEditDto, @MappingTarget Event event);

    protected User findUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    protected List<Skill> findSkillsByIds(List<Long> ids) {
        return skillRepository.findAllById(ids);
    }
}
