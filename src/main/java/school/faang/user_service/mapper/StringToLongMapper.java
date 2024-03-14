package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface StringToLongMapper {
    StringToLongMapper INSTANCE = Mappers.getMapper( StringToLongMapper.class );

    @Mapping(target = "skills", expression = "java(convertSkillsToListLong(value))")
    List<Long> stringToLong(List<String> skills);

    default List<Long> convertSkillsToListLong(List<String> skills) {
        return skills.stream()
                .map(Long::valueOf)
                .toList();
    }
}
