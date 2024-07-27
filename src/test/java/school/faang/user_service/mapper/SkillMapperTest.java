package school.faang.user_service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SkillMapperTest {
    private SkillMapper mapper;
    Skill firstEntity, secondEntity;
    SkillDto firstDto, secondDto;
    User firstUser, secondUser;
    LocalDateTime firstDateTime, secondDateTime;

    @BeforeEach
    void setUp() {
        mapper = new SkillMapperImpl();

        firstDateTime = LocalDateTime.of(2024, 7, 24, 18, 5);
        secondDateTime = LocalDateTime.of(2024, 7, 24, 19, 25);

        firstUser = new User();
        firstUser.setId(1L);
        secondUser = new User();
        secondUser.setId(2L);

        firstEntity = new Skill();
        firstEntity.setId(1L);
        firstEntity.setTitle("firstTitle");
        firstEntity.setUsers(List.of(firstUser, secondUser));
        firstEntity.setCreatedAt(firstDateTime);
        firstEntity.setUpdatedAt(secondDateTime);

        secondEntity = new Skill();
        secondEntity.setId(2L);
        secondEntity.setTitle("secondTitle");
        secondEntity.setUsers(List.of(secondUser));
        secondEntity.setCreatedAt(firstDateTime);
        secondEntity.setUpdatedAt(secondDateTime);

        firstDto = new SkillDto();
        firstDto.setId(firstEntity.getId());
        firstDto.setTitle(firstEntity.getTitle());
        firstDto.setUserIds(List.of(firstUser.getId(), secondUser.getId()));
        firstDto.setCreatedAt(firstEntity.getCreatedAt());
        firstDto.setUpdatedAt(firstEntity.getUpdatedAt());

        secondDto = new SkillDto();
        secondDto.setId(secondEntity.getId());
        secondDto.setTitle(secondEntity.getTitle());
        secondDto.setUserIds(List.of(secondUser.getId()));
        secondDto.setCreatedAt(secondEntity.getCreatedAt());
        secondDto.setUpdatedAt(secondEntity.getUpdatedAt());
    }

    @Test
    void testToDto() {
        assertEquals(firstDto, mapper.toDto(firstEntity));
    }

    @Test
    void testListToDtoList() {
        List<SkillDto> dtos = List.of(firstDto, secondDto);
        List<Skill> entities = List.of(firstEntity, secondEntity);

        assertEquals(dtos, mapper.toDto(entities));
    }

    @Test
    void testToEntity() {
        Skill entity = mapper.toEntity(firstDto);

        assertEquals(firstEntity.getId(), entity.getId());
        assertEquals(firstEntity.getTitle(), entity.getTitle());
        assertEquals(firstEntity.getCreatedAt(), entity.getCreatedAt());
        assertEquals(firstEntity.getUpdatedAt(), entity.getUpdatedAt());
    }

    @Test
    void testUsersToUserIds() {
        List<User> users = List.of(firstUser, secondUser);
        List<Long> userIds = List.of(firstUser.getId(), secondUser.getId());

        assertEquals(userIds, mapper.usersToUserIds(users));
    }

    @Test
    void testUsersToUserIdsWithNullInput() {
        assertEquals(new ArrayList<>(), mapper.usersToUserIds(null));
    }
}