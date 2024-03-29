package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StringToLongMapper {
    StringToLongMapper INSTANCE = Mappers.getMapper( StringToLongMapper.class );

    @Mapping(target = "skills", expression = "java(convertSkillsToListLong(value))")
    List<Long> stringToLong(List<String> skills);

    default List<Long> convertSkillsToListLong(List<String> skills) {
        if (Objects.isNull(skills)) {
            return null;
        }
        return skills.stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());
    }
}
