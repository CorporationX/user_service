package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

    @InjectMocks
    private SkillService skillService;

    private final SkillMapper skillMapper = SkillMapper.INSTANCE;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testExistsByTitle() {
        SkillDto skillDto = new SkillDto(1L, "crek");
        skillService.create(skillDto);
        Assert.assertThrows(
                DataValidationException.class,
                () -> skillService.create(new SkillDto(2L, "crek"))
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