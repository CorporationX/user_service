package school.faang.user_service.service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.mapper.SkillDtoSkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillDtoSkillMapper skillMapper;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SkillService skillService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @ParameterizedTest
    @MethodSource("skillDtoProvider")
    public void testCreateSkill(SkillDto skillDto, boolean existsByTitle) {
        // Arrange
        Skill skill = Skill.builder().id(skillDto.getId()).title(skillDto.getTitle()).build();

        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(existsByTitle);
        when(skillMapper.toSkill(skillDto)).thenReturn(skill);
        when(skillRepository.save(skill)).thenReturn(skill);
        when(skillMapper.toSkillDto(skill)).thenReturn(skillDto);

        // Act
        SkillDto createdSkill = skillService.create(skillDto);

        // Assert
        assertNotNull(createdSkill);
        assertEquals(skillDto, createdSkill);
        verify(skillRepository, times(1)).existsByTitle(skillDto.getTitle());
        verify(skillMapper, times(1)).toSkill(skillDto);
        verify(skillRepository, times(1)).save(skill);
        verify(skillMapper, times(1)).toSkillDto(skill);
    }

    private static Stream<Object[]> skillDtoProvider() {
        return Stream.of(
                new Object[]{new SkillDto(1L, "Russian"), true},
                new Object[]{new SkillDto(2L, "Python"), true},
                new Object[]{new SkillDto(3L, "JavaScript"), true}
        );
    }
}