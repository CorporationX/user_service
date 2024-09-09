package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.SkillCustomMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.test_data.event.TestDataEvent;

import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillCustomMapper skillMapper;
    @InjectMocks
    private SkillService skillService;

    private TestDataEvent testDataEvent;

    @BeforeEach
    void setUp() {
        testDataEvent = new TestDataEvent();
    }

    @Test
    public void testGetUserSkillsList_Success() {
        User user = testDataEvent.getUser();
        Skill skill1 = testDataEvent.getSkill1();
        SkillDto skillDto = testDataEvent.getSkillDto1();
        List<Skill> ownerSkillsList = List.of(skill1);

        when(skillRepository.findAllByUserId(user.getId())).thenReturn(ownerSkillsList);
        when(skillMapper.toDto(skill1)).thenReturn(skillDto);

        List<SkillDto> result = skillService.getUserSkillsList(user.getId());
        assertNotNull(result);
        assertEquals(ownerSkillsList.size(), result.size());
        assertEquals(ownerSkillsList.get(0).getTitle(), result.get(0).getTitle());

        verify(skillRepository, atLeastOnce()).findAllByUserId(user.getId());
        verify(skillMapper, atLeastOnce()).toDto(skill1);
    }
}
