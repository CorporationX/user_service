package school.faang.user_service.mapper.skill;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.SkillOffer;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.skill.EventSkillOfferedDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class EventSkillOfferedMapperTest {
    @Spy
    private EventSkillOfferedMapperImpl mapper;

    @Test
    void toDto_ShouldMapEntityToDto() {
        SkillOffer entity = new SkillOffer();
        entity.setId(1L);

        Skill skill = new Skill();
        skill.setId(2L);
        entity.setSkill(skill);

        User author = new User();
        author.setId(3L);
        entity.setAuthor(author);

        User receiver = new User();
        receiver.setId(4L);
        entity.setReceiver(receiver);

        EventSkillOfferedDto dto = mapper.toDto(entity);

        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getSkillOfferedId());
        assertEquals(3L, dto.getAuthorId());
        assertEquals(4L, dto.getReceiverId());
    }

    @Test
    void toDto_ShouldMapNullEntityToNullDto() {
        EventSkillOfferedDto dto = mapper.toDto(null);

        assertNull(dto);
    }

    @Test
    void toEntity_ShouldMapDtoToEntity() {
        EventSkillOfferedDto dto = new EventSkillOfferedDto();
        dto.setId(1L);
        dto.setSkillOfferedId(2L);
        dto.setAuthorId(3L);
        dto.setReceiverId(4L);

        SkillOffer entity = mapper.toEntity(dto);

        assertEquals(1L, entity.getId());
        assertEquals(2L, entity.getSkill().getId());
        assertEquals(3L, entity.getAuthor().getId());
        assertEquals(4L, entity.getReceiver().getId());
    }

    @Test
    void toEntity_ShouldMapNullDtoToNullEntity() {
        SkillOffer entity = mapper.toEntity(null);

        assertNull(entity);
    }
}