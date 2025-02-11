package school.faang.user_service.mapper.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.event.SkillDto;
import school.faang.user_service.entity.Skill;

@Mapper(componentModel = "spring")
public interface EventSkillMapper {

  @Mapping(target = "id", source = "skill.id")
  @Mapping(target = "name", source = "skill.title")
  SkillDto toDto(Skill skill);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "title", source = "skillDto.name")
  @Mapping(target = "users", ignore = true)
  @Mapping(target = "guarantees", ignore = true)
  @Mapping(target = "events", ignore = true)
  @Mapping(target = "goals", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Skill toEntity(SkillDto skillDto);
}
