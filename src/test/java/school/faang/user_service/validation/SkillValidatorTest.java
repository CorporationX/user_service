package school.faang.user_service.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.config.properties.SkillProperties;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SkillValidatorTest {

    private static final long SKILL_ID = 10L;
    private static final long USER_ID = 5L;
    private static final long AUTHOR_ID_1 = 100L;
    private static final long AUTHOR_ID_2 = 200L;
    private static final long AUTHOR_ID_3 = 300L;

    private SkillValidator skillValidator;

    @BeforeEach
    void setUp() {
        SkillProperties skillProperties = new SkillProperties(3);
        skillValidator = new SkillValidator(skillProperties);
    }

    @Test
    @DisplayName("Throws if skill title already exists")
    void validateSkillTitleIsUniqueThrowsExceptionWhenExists() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> skillValidator.validateSkillTitleIsUnique(true, "Java"));

        assertMessageContains(exception, "Skill with title: Java already exists.");
    }

    @Test
    @DisplayName("Does not throw if skill title is unique")
    void validateSkillTitleIsUniqueDoesNothingWhenNotExists() {
        assertDoesNotThrow(() -> skillValidator.validateSkillTitleIsUnique(false, "Java"));
    }

    @Test
    @DisplayName("Throws if skill does not exist")
    void ensureSkillExistsThrowsException() {
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> skillValidator.ensureSkillExists(false, SKILL_ID));

        assertMessageContains(exception, "Skill with id " + SKILL_ID + " does not exist.");
    }

    @Test
    @DisplayName("Does not throw if skill exists")
    void ensureSkillExistsDoesNothingWhenExists() {
        assertDoesNotThrow(() -> skillValidator.ensureSkillExists(true, SKILL_ID));
    }

    @Test
    @DisplayName("Throws if user already has the skill")
    void validateUserDoesNotHaveSkillThrowsException() {
        ForbiddenException exception = assertThrows(ForbiddenException.class,
                () -> skillValidator.validateUserDoesNotHaveSkill(true, SKILL_ID, USER_ID));

        assertMessageContains(exception, "User with id " + USER_ID + " already has skill with id " + SKILL_ID);
    }

    @Test
    @DisplayName("Does not throw if user does not have the skill")
    void validateUserDoesNotHaveSkillDoesNothing() {
        assertDoesNotThrow(() -> skillValidator.validateUserDoesNotHaveSkill(false, SKILL_ID, USER_ID));
    }

    @Test
    @DisplayName("Throws if not enough unique offers exist")
    void validateEnoughSkillOffersThrowsException() {
        SkillOffer offer = createSkillOffer(AUTHOR_ID_1);

        ForbiddenException exception = assertThrows(ForbiddenException.class,
                () -> skillValidator.validateEnoughSkillOffers(List.of(offer)));

        assertMessageContains(
                exception,
                "Skill cannot be acquired. At least 3 unique users must offer this skill."
        );
    }

    @Test
    @DisplayName("Does not throw if enough unique offers exist")
    void validateEnoughSkillOffersDoesNothing() {
        SkillOffer offer1 = createSkillOffer(AUTHOR_ID_1);
        SkillOffer offer2 = createSkillOffer(AUTHOR_ID_2);
        SkillOffer offer3 = createSkillOffer(AUTHOR_ID_3);

        assertDoesNotThrow(() -> skillValidator.validateEnoughSkillOffers(List.of(offer1, offer2, offer3)));
    }

    private SkillOffer createSkillOffer(long authorId) {
        User author = User.builder()
                .id(authorId)
                .build();
        Recommendation recommendation = Recommendation.builder()
                .author(author)
                .build();

        return SkillOffer.builder()
                .recommendation(recommendation)
                .build();
    }

    private void assertMessageContains(Exception exception, String expected) {
        assertNotNull(exception.getMessage());
        assertThat(exception.getMessage()).contains(expected);
    }
}
