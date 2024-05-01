package school.faang.user_service.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.service.SkillService;

import static org.junit.jupiter.api.Assertions.assertThrows;
@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {
    @InjectMocks
    private SkillService skillService;
    @Mock
    private SkillRepository skillRepository;
    @Spy
    private SkillMapper skillMapper;
    SkillDto skill;

    @BeforeEach
    public void init() {
        skill = new SkillDto();
        skill.setId(1L);
        skill.setTitle("Driving a car");
    }

    @Test
    public void testCreateWithBlankTitle() {
        skill.setTitle("   ");
        System.out.println("skillService" + skillService);
        assertThrows(DataValidationException.class, () -> skillService.create(skill));
    }

    @Test
    public void testSkillSave() throws DataValidationException {
        skillService.create(skill);
        Mockito.verify(skillRepository, Mockito.times(1)).save(skillMapper.toEntity(skill));
    }

    @Test
    public void testCreateIfSkillExist() {
        Mockito.when(skillRepository.existsByTitle(skill.getTitle())).thenReturn(true);
        assertThrows(DataValidationException.class, () -> skillService.create(skill));
    }
}