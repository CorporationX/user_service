package school.faang.user_service.mapper.skill;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.EventSkillOfferedDto;
import school.faang.user_service.entity.recommendation.SkillOffer;

@Mapper(componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventSkillOfferedMapper {

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    @Mapping(target = "skillOfferedId", source = "skill.id")
    EventSkillOfferedDto toDto(SkillOffer entity);

    @Mapping(target = "author.id", source = "authorId")
    @Mapping(target = "receiver.id", source = "receiverId")
    @Mapping(target = "skill.id", source = "skillOfferedId")
    SkillOffer toEntity(EventSkillOfferedDto dto);
}