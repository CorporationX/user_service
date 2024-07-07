package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.SkillRequestDto;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.List;

@Mapper(uses = {SkillMapper.class})
public interface SkillRequestMapper {

    SkillRequestMapper INSTANCE = Mappers.getMapper(SkillRequestMapper.class);

    @Mapping(target = "request", ignore = true)
    @Mapping(source = "skillDto", target = "skill")
    SkillRequest dtoToEntity(SkillRequestDto skillRequestDto);

    @Mapping(source = "skill", target = "skillDto")
    SkillRequestDto entityToDto(SkillRequest skillRequest);

    List<SkillRequest> dtoListToEntityList(List<SkillRequestDto> skillRequestDtoList);
    List<SkillRequestDto> entityListToDtoList(List<SkillRequest> skillRequestList);
}
