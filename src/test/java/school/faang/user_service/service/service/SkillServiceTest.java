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
import school.faang.user_service.entity.Skill;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.validator.SkillValidator;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {
    private final long TEST_ID = 1L;
    private final String TEST_SKILL = "Java";
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillValidator skillValidator;
    @Spy
    private SkillMapper skillMapper;

    @InjectMocks
    private SkillService skillService;

    private SkillDto skillDto;
    private Skill skill;

    @BeforeEach
    public void init() {
        skillDto = new SkillDto();
        skillDto.setId(TEST_ID);
        skillDto.setTitle(TEST_SKILL);

        skill = skillMapper.toEntity(skillDto);

    }

    @Test
    public void isSkillRepositorySaved() {
        skillService.create(skillDto);
        Mockito.verify(skillRepository, Mockito.times(1)).save(skill);
    }
}
