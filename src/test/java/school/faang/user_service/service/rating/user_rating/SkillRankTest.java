package school.faang.user_service.service.rating.user_rating;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.rating.UserRatingDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.rating.UserRating;
import school.faang.user_service.entity.rating.UserRatingType;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.rating.RatingTypeService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillRankTest {
    @Mock
    UserService userService;
    @Mock
    RatingTypeService ratingTypeService;
    @Mock
    SkillService skillService;
    @InjectMocks
    SkillRank skillRank;

    @Test
    void isApplicable() {
        UserRatingDto userRatingDto = UserRatingDto.builder()
                .skillRating(true)
                .build();
        assertTrue(skillRank.isApplicable(userRatingDto));
    }

    @Test
    void isApplicableFalse() {
        UserRatingDto userRatingDto = UserRatingDto.builder()
                .skillRating(false)
                .build();
        assertFalse(skillRank.isApplicable(userRatingDto));
    }

    @Test
    void isApplicableIsNull() {
        assertFalse(skillRank.isApplicable(null));
    }

    @Test
    void calculate() {
        List<Long> userIds = Arrays.asList(1L, 2L);

        UserRatingDto ratings = UserRatingDto.builder()
                .skillRating(true)
                .build();

        UserRatingType ratingType = UserRatingType.builder()
                .name("Skill rating")
                .cost(10)
                .build();

        when(ratingTypeService.findByName("Skill rating")).thenReturn(ratingType);

        when(skillService.getUserSkills(1L)).thenReturn(List.of(Skill.builder().build()));
        when(skillService.getUserSkills(2L)).thenReturn(List.of(Skill.builder().build(),
                Skill.builder().build()));

        when(userService.getUser(1L)).thenReturn(User.builder().id(1L).build());
        when(userService.getUser(2L)).thenReturn(User.builder().id(2L).build());

        List<UserRating> actualRatings = skillRank.calculate(userIds, ratings);

        assertEquals(2, actualRatings.size());
        assertEquals(1, actualRatings.get(0).getUser().getId());
        assertEquals(2, actualRatings.get(1).getUser().getId());
        assertEquals(10, actualRatings.get(0).getScore());
        assertEquals(20, actualRatings.get(1).getScore());
        assertEquals("Skill rating", actualRatings.get(0).getType().getName());
        assertEquals("Skill rating", actualRatings.get(1).getType().getName());

        verify(ratingTypeService, times(1)).findByName("Skill rating");
        verify(skillService, times(2)).getUserSkills(anyLong());
        verify(userService, times(2)).getUser(anyLong());
    }

    @Test
    void calculateUserIdsIsNull() {
        assertThrows(DataValidationException.class, () -> skillRank.calculate(null,
                UserRatingDto.builder()
                        .build()));
    }

    @Test
    void calculateUserIdsIsEmpty() {
        assertThrows(DataValidationException.class, () -> skillRank.calculate(List.of(),
                UserRatingDto.builder()
                        .build()));
    }

    @Test
    void calculateRatingsIsNull() {
        assertThrows(DataValidationException.class, () -> skillRank.calculate(List.of(1L, 2L), null));
    }
}