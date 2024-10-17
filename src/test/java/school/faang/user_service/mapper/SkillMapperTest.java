package school.faang.user_service.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.model.dto.SkillDto;
import school.faang.user_service.model.entity.Skill;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SkillMapperTest {
    private final SkillMapper mapper = new SkillMapperImpl();

    @Test
    @DisplayName("mapping to dto")
    public void toSkillDtoTest() {
        Skill entity = getEntity(1L);
        SkillDto dto = mapper.toSkillDto(entity);

        assertAll(
                () -> assertNotNull(dto),
                () -> assertEquals(entity.getId(), dto.getId()),
                () -> assertEquals(entity.getTitle(), dto.getTitle())
        );
    }

    @Test
    @DisplayName("mapping to dto, null input")
    public void toSkillDtoNullTest() {
        SkillDto dto = mapper.toSkillDto(null);

        assertNull(dto);
    }

    @Test
    @DisplayName("mapping to entity")
    public void toSkillEntityTest() {
        SkillDto dto = getDto(108L);
        Skill entity = mapper.toSkillEntity(dto);

        assertAll(
                () -> assertNotNull(entity),
                () -> assertEquals(0, entity.getId()),
                () -> assertEquals(dto.getTitle(), entity.getTitle())
        );
    }

    @Test
    @DisplayName("mapping to entity, null input")
    public void toSkillEntityNullTest() {
        Skill entity = mapper.toSkillEntity(null);

        assertNull(entity);
    }

    @Test
    @DisplayName("mapping list to dto")
    public void toListDtoTest() {
        Skill entity = getEntity(1L);
        Skill anotherEntity = getEntity(2L);
        List<Skill> entities = Arrays.asList(entity, anotherEntity);
        List<SkillDto> dtos = mapper.toListSkillDto(entities);

        assertAll(
                () -> assertNotNull(dtos),
                () -> assertEquals(entities.size(), dtos.size()),
                () -> assertEquals(entities.get(0).getId(), dtos.get(0).getId()),
                () -> assertEquals(entities.get(0).getTitle(), dtos.get(0).getTitle()),
                () -> assertEquals(entities.get(1).getId(), dtos.get(1).getId()),
                () -> assertEquals(entities.get(1).getTitle(), dtos.get(1).getTitle())
        );
    }

    @Test
    @DisplayName("mapping list to dto, null input")
    public void toListSkillDtoNullTest() {
        List<SkillDto> dtos = mapper.toListSkillDto(null);

        assertNull(dtos);
    }

    private Skill getEntity(Long id) {
        Skill entity = new Skill();
        entity.setId(id);
        entity.setTitle("FAANG");
        return entity;
    }

    private SkillDto getDto(Long id) {
        SkillDto dto = new SkillDto();
        dto.setId(id);
        dto.setTitle("Java");
        return dto;
    }
}
