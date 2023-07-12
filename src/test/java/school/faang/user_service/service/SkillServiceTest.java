package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mappers.SkillMapper;
import school.faang.user_service.repository.SkillRepository;


@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @InjectMocks
    private SkillService skillService;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillMapper skillMapper;


    @Test
    void testCreate() {
        skillService.create(new SkillDto());
        Mockito.verify(skillRepository, Mockito.times(1)).save(skillMapper.toEntity(new SkillDto()));
    }

    @Test
    void testCreateExistByTitle() {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle("privet");
        Mockito.when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(true);
        Assert.assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

}