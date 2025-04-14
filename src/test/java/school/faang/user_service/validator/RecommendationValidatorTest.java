package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recomendation.RecommendationDto;
import school.faang.user_service.dto.recomendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationValidatorTest {

    private static final Long AUTOR_ID = 1L;
    private static final Long RECIVER_ID = 2L;
    private static final Long SKILL_ID_1 = 1L;
    private static final Long SKILL_ID_2 = 2L;
    private static final Long NON_EXISTENT_SKILL_ID = 4L;
    private static final LocalDateTime OLD_DATE = LocalDateTime.of(2025, Month.FEBRUARY, 6,
            21, 51, 48, 311_000_000);

    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillRepository skillRepository;
    @InjectMocks
    private RecommendationValidator recommendationValidator;

    @Test
    public void testTextAvailabilityWithEmptyContentM1(){
        RecommendationDto recommendationDto = RecommendationDto.builder()
                .authorId(AUTOR_ID).receiverId(RECIVER_ID).content("")
                .build();

        assertThrows(DataValidationException.class, () -> recommendationValidator.textAvailability(recommendationDto),
                "Не отработал метод textAvailability - isEmpty()");
    }

    @Test
    public void testTextAvailabilityWithContentM1(){
        RecommendationDto recommendationDto = RecommendationDto.builder()
                .authorId(AUTOR_ID).receiverId(RECIVER_ID).content("Контент")
                .build();
        assertDoesNotThrow(() -> recommendationValidator.textAvailability(recommendationDto));
    }

    @Test
    public void testCheckRecommendationIntervalBadTimeM2() {
        LocalDateTime newDate = LocalDateTime.of(2025, Month.FEBRUARY, 7,
                21, 51, 48, 311_000_000);
        RecommendationDto dto = setRecommendationDto(newDate);
        Recommendation lastRecommendation = setRecommendation(OLD_DATE);

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(AUTOR_ID, RECIVER_ID))
                .thenReturn(Optional.of(lastRecommendation));

        assertThrows(DataValidationException.class,
                () -> recommendationValidator.checkRecommendationInterval(dto)
        );
    }

    @Test
    public void testCheckRecommendationIntervalGoodTimeM2() {
        LocalDateTime newDate = LocalDateTime.of(2033, Month.FEBRUARY, 7,
                21, 51, 48, 311_000_000);
        RecommendationDto dto = setRecommendationDto(newDate);
        Recommendation lastRecommendation = setRecommendation(OLD_DATE);

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(AUTOR_ID, RECIVER_ID))
                .thenReturn(Optional.of(lastRecommendation));

        assertDoesNotThrow(() -> recommendationValidator.checkRecommendationInterval(dto));
    }

    @Test
    public void testCheckingSkillsAllSkillsExistReturnsTruesM3(){
        RecommendationDto dto = createDto(List.of(SKILL_ID_1, SKILL_ID_2));
        List<Skill> existingSkills = List.of(
                createSkill(SKILL_ID_1),
                createSkill(SKILL_ID_2)
        );

        when(skillRepository.findAll()).thenReturn(existingSkills);

        assertTrue(recommendationValidator.checkingSkills(dto));
    }

    @Test
    public void testCheckingSkillsSkillNotExistsThrowsExceptionM3(){
        RecommendationDto dto = createDto(List.of(SKILL_ID_1, NON_EXISTENT_SKILL_ID));
        List<Skill> existingSkills = List.of(
                createSkill(SKILL_ID_1),
                createSkill(SKILL_ID_2)
        );

        when(skillRepository.findAll()).thenReturn(existingSkills);

        assertThrows(DataValidationException.class,
                () -> recommendationValidator.checkingSkills(dto)
        );
    }

    @Test
    public void testCheckingSkillsEmptyRecommendedSkillsReturnsTrue() {
        RecommendationDto dto = createDto(List.of());

        assertTrue(recommendationValidator.checkingSkills(dto));
    }

    @Test
    public void testCheckingSkillsNoSkillsInDbThrowsException() {
        RecommendationDto dto = createDto(List.of(SKILL_ID_1, NON_EXISTENT_SKILL_ID));
        List<Skill> existingSkills = List.of();

        when(skillRepository.findAll()).thenReturn(existingSkills);

        assertThrows(DataValidationException.class, () -> recommendationValidator.checkingSkills(dto));
    }

    private RecommendationDto setRecommendationDto(LocalDateTime newDate){
        return RecommendationDto.builder()
                .authorId(AUTOR_ID).receiverId(RECIVER_ID).createdAt(newDate)
                .build();
    }

    private Recommendation setRecommendation(LocalDateTime oldDate){
        Recommendation lastRecommendation = new Recommendation();
        lastRecommendation.setCreatedAt(oldDate);
        return lastRecommendation;
    }

    private RecommendationDto createDto(List<Long> skillIds) {
        List<SkillOfferDto> skillOffers = skillIds.stream()
                .map(id -> SkillOfferDto.builder().skillId(id).build())
                .toList();

        return RecommendationDto.builder()
                .skillOffers(skillOffers)
                .build();
    }

    private Skill createSkill(Long id) {
        return Skill.builder()
                .id(id)
                .build();
    }
}