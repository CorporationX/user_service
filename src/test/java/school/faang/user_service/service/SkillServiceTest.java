package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillMapper skillMapper;

    @InjectMocks
    private SkillService skillService;

    @Test
    void testExistsByTitle() {
        Mockito.when(skillRepository.existsByTitle("crek")).thenReturn(true);

        Assert.assertThrows(
                DataValidationException.class,
                () -> skillService.create(new SkillDto(1L, "crek"))
        );
    }

    @Test
    void testCreate() {
        SkillDto skillDto = new SkillDto(1L, "crek");
        skillService.create(skillDto);
        Mockito.verify(skillRepository, Mockito.times(1))
                .save(skillMapper.toEntity(skillDto));
    }
}