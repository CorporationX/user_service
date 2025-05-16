package school.faang.user_service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FollowerExperienceFilterTest {
    private final FollowerExperienceFilter experienceFilter = new FollowerExperienceFilter();
    private final User user = new User();
    private final UserFilterDto filter = new UserFilterDto();

    @Nested
    @DisplayName("when only minimum is specified")
    class OnlyMin {
        @BeforeEach
        void setMin() {
            filter.setExperienceMin(10);
            filter.setExperienceMax(null);
        }

        @Test
        @DisplayName("experience equal to min should return true")
        void equalToMinShouldReturnTrue() {
            user.setExperience(10);
            assertTrue(experienceFilter.apply(user, filter));
        }

        @Test
        @DisplayName("experience above min should return true")
        void aboveMinShouldReturnTrue() {
            user.setExperience(15);
            assertTrue(experienceFilter.apply(user, filter));
        }

        @Test
        @DisplayName("experience below min should return false")
        void belowMinShouldReturnTrue() {
            user.setExperience(5);
            assertFalse(experienceFilter.apply(user, filter));
        }
    }

    @Nested
    @DisplayName("when only maximum is specified")
    class OnlyMax {
        @BeforeEach
        void setMax() {
            filter.setExperienceMin(null);
            filter.setExperienceMax(10);
        }

        @Test
        @DisplayName("experience equal to max should return true")
        void equalToMaxShouldReturnTrue() {
            user.setExperience(10);
            assertTrue(experienceFilter.apply(user, filter));
        }

        @Test
        @DisplayName("experience below max should return true")
        void belowMaxShouldReturnTrue() {
            user.setExperience(5);
            assertTrue(experienceFilter.apply(user, filter));
        }

        @Test
        @DisplayName("experience above max should return false")
        void aboveMaxShouldReturnFalse() {
            user.setExperience(15);
            assertFalse(experienceFilter.apply(user, filter));
        }
    }

    @Nested
    @DisplayName("when both bounds are specified")
    class BothBounds {
        @BeforeEach
        void setBounds() {
            filter.setExperienceMin(10);
            filter.setExperienceMax(20);
        }

        @Test
        @DisplayName("experience within range should return true")
        void withinRangeShouldReturnTrue() {
            user.setExperience(15);
            assertTrue(experienceFilter.apply(user, filter));
        }

        @Test
        @DisplayName("experience equal to min should return true")
        void equalToMinShouldReturnTrue() {
            user.setExperience(10);
            assertTrue(experienceFilter.apply(user, filter));
        }

        @Test
        @DisplayName("experience equal to max should return true")
        void equalToMaxShouldReturnTrue() {
            user.setExperience(20);
            assertTrue(experienceFilter.apply(user, filter));
        }

        @Test
        @DisplayName("experience below min should return false")
        void belowMinShouldReturnFalse() {
            user.setExperience(5);
            assertFalse(experienceFilter.apply(user, filter));
        }

        @Test
        @DisplayName("experience above max should return false")
        void aboveMaxShouldReturnFalse() {
            user.setExperience(25);
            assertFalse(experienceFilter.apply(user, filter));
        }

        @Test
        @DisplayName("invalid range min > max should return false for any experience")
        void invalidRangeAlwaysFalse() {
            user.setExperience(15);
            filter.setExperienceMin(20);
            filter.setExperienceMax(10);
            assertFalse(experienceFilter.apply(user, filter));
        }
    }

    @Test
    @DisplayName("min and max null should return true for any experience")
    void nullExperienceShouldReturnTrue() {
        filter.setExperienceMin(null);
        filter.setExperienceMax(null);
        assertTrue(experienceFilter.apply(user, filter));
    }

    @Test
    @DisplayName("experience is null with present bounds should return false")
    void nullExperienceShouldReturnFalseWhenBoundsArePresent() {
        user.setExperience(null);
        filter.setExperienceMin(10);
        filter.setExperienceMax(20);
        assertFalse(experienceFilter.apply(user, filter));
    }
}
