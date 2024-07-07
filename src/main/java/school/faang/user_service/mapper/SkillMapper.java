package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;

@Mapper
public interface SkillMapper {

    SkillMapper INSTANCE = Mappers.getMapper(SkillMapper.class);

    SkillDto entityToDto(Skill skill);

    @Mapping(target = "users", ignore = true)
    @Mapping(target = "guarantees", ignore = true)
    @Mapping(target = "events", ignore = true)
    @Mapping(target = "goals", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Skill dtoToEntity(SkillDto skillDto);
}
