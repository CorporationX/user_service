package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationValidatorTest {
    @InjectMocks
    private RecommendationValidator validator;
    @Mock
    RecommendationRepository recommendationRepository;
    @Mock
    SkillRepository skillRepository;

    @ParameterizedTest
    @MethodSource("getInvalidContents")
    public void testInvalidContents(String content) {
        Assertions.assertThrows(
                DataValidationException.class,
                () -> validator.validateContent(content)
        );
    }

    @Test
    public void testInvalidDate() {
        LocalDateTime almostSixMonthAgo = LocalDateTime.now().minusDays(175);
        long id = 1;

        RecommendationDto dto = RecommendationDto.builder()
                .authorId(id)
                .receiverId(id)
                .build();

        Recommendation recommendation = Recommendation.builder()
                .createdAt(almostSixMonthAgo)
                .build();

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(id, id))
                .thenReturn(Optional.of(recommendation));

        Assertions.assertThrows(
                DataValidationException.class,
                () -> validator.checkDate(dto)
        );
    }

    @Test
    public void testValidDate() {
        LocalDateTime beforeSixMonthAgo = LocalDateTime.now()
                .minusMonths(6)
                .minusDays(1);
        long id = 1;

        RecommendationDto dto = RecommendationDto.builder()
                .authorId(id)
                .receiverId(id)
                .build();

        Recommendation recommendation = Recommendation.builder()
                .createdAt(beforeSixMonthAgo)
                .build();

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(id, id))
                .thenReturn(Optional.of(recommendation));

        Assertions.assertDoesNotThrow(
                () -> validator.checkDate(dto)
        );
    }

    @Test
    public void testFirstRecommendationNoDate() {
        long id = 1;
        RecommendationDto dto = RecommendationDto.builder()
                .authorId(id)
                .receiverId(id)
                .build();

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(id, id))
                .thenReturn(Optional.empty());

        Assertions.assertDoesNotThrow(
                () -> validator.checkDate(dto)
        );
    }

    @Test
    public void testDuplicateSkills() {
        List<SkillOfferDto> list = List.of(getSkillOfferDto(), getSkillOfferDto());

        Assertions.assertThrows(
                DataValidationException.class,
                () -> validator.checkDuplicateSkills(list)
        );
    }

    @Test
    public void testUniqueSkills() {
        List<SkillOfferDto> list = List.of(getSkillOfferDto());

        Assertions.assertDoesNotThrow(
                () -> validator.checkDuplicateSkills(list)
        );
    }

    @Test
    public void testNonExistingSkills() {
        List<SkillOfferDto> skills = List.of(getSkillOfferDto());
        when(skillRepository.countExisting(List.of(getSkillOfferDto().getSkillId()))).thenReturn(0);

        Assertions.assertThrows(
                DataValidationException.class,
                () -> validator.checkExistAllSkills(skills)
        );
    }

    @Test
    public void testExistingSkills() {
        List<SkillOfferDto> skills = List.of(getSkillOfferDto());
        when(skillRepository.countExisting(List.of(getSkillOfferDto().getSkillId()))).thenReturn(1);

        Assertions.assertDoesNotThrow(
                () -> validator.checkExistAllSkills(skills)
        );
    }

    @Test
    public void testEqualsAuthorAndReceiverId() {
        long id = 1;

        Assertions.assertThrows(
                DataValidationException.class,
                () -> validator.checkAuthorAndReceiverId(id, id)
        );
    }

    @Test
    public void testUniqueAuthorAndReceiverId() {
        long authorId = 1;
        long receiverId = 2;

        Assertions.assertDoesNotThrow(
                () -> validator.checkAuthorAndReceiverId(authorId, receiverId)
        );
    }

    private static Stream<Object[]> getInvalidContents() {
        return Stream.of(
                new Object[]{null},
                new Object[]{""},
                new Object[]{"  "}
        );
    }

    private SkillOfferDto getSkillOfferDto() {
        return SkillOfferDto.builder()
                .skillId(1L)
                .build();
    }
}