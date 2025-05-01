package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.recommendation.RecommendationController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.recommendation.RecommendationService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationControllerTest {

    @Mock
    private RecommendationService recommendationService;
    @InjectMocks
    private RecommendationController recommendationController;

    private static final long RECEIVER_ID = 1L;
    private static final long AUTHOR_ID = 1L;
    private static final long INVALID_ID = 999L;

    private RecommendationDto recommendation1;
    private RecommendationDto recommendation2;

    @BeforeEach
    public void setUp() {
        recommendation1 = createRecommendationDto(1L, "First Recommendation");
        recommendation2 = createRecommendationDto(2L, "Second Recommendation");
    }

    @Nested
    class GiveRecommendationTests {
        @Test
        public void testGiveRecommendationSuccess() {
            RecommendationDto inputRecommendation = createRecommendationDto(null, "Valid Content");
            RecommendationDto expectedRecommendation = createRecommendationDto(null, "Valid Content");

            when(recommendationService.create(inputRecommendation)).thenReturn(expectedRecommendation);

            RecommendationDto result = recommendationController.giveRecommendation(inputRecommendation);

            assertAll(
                    () -> assertNotNull(result),
                    () -> assertEquals(expectedRecommendation.getContent(), result.getContent())
            );

            verify(recommendationService, times(1)).create(inputRecommendation);
        }

        @ParameterizedTest
        @NullSource
        @EmptySource
        @ValueSource(strings = {"   "})
        public void testGiveRecommendationInvalidContent(String content) {
            RecommendationDto invalidRecommendation = createRecommendationDto(null, content);

            assertThrows(DataValidationException.class, () -> {
                recommendationController.giveRecommendation(invalidRecommendation);
            });

            verify(recommendationService, never()).create(any());
        }
    }

    @Nested
    class UpdateRecommendationTests {
        @Test
        public void testUpdateRecommendationSuccess() {
            RecommendationDto inputRecommendation = createRecommendationDto(1L, "Updated Content");
            RecommendationDto expectedRecommendation = createRecommendationDto(1L, "Updated Content");

            when(recommendationService.update(inputRecommendation)).thenReturn(expectedRecommendation);

            RecommendationDto result = recommendationController.updateRecommendation(inputRecommendation);

            assertAll(
                    () -> assertNotNull(result),
                    () -> assertEquals(expectedRecommendation.getId(), result.getId()),
                    () -> assertEquals(expectedRecommendation.getContent(), result.getContent())
            );

            verify(recommendationService, times(1)).update(inputRecommendation);
        }
    }

    @Nested
    class DeleteRecommendationTests {
        @Test
        public void testDeleteRecommendationSuccess() {
            recommendationController.deleteRecommendation(RECEIVER_ID);

            verify(recommendationService, times(1)).delete(RECEIVER_ID);
        }

        @Test
        public void testDeleteRecommendationServiceThrowsException() {
            doThrow(new RuntimeException("Recommendation not found")).when(recommendationService).delete(INVALID_ID);

            Exception exception = assertThrows(RuntimeException.class, () -> {
                recommendationController.deleteRecommendation(INVALID_ID);
            });

            assertEquals("Recommendation not found", exception.getMessage());

            verify(recommendationService, times(1)).delete(INVALID_ID);
        }
    }

    @Nested
    class GetAllUserRecommendationsTests {
        @Test
        public void testGetAllUserRecommendationsSuccess() {
            List<RecommendationDto> expectedRecommendations = Arrays.asList(recommendation1, recommendation2);

            when(recommendationService.getAllUserRecommendations(RECEIVER_ID)).thenReturn(expectedRecommendations);

            List<RecommendationDto> result = recommendationController.getAllUserRecommendations(RECEIVER_ID);

            assertAll(
                    () -> assertNotNull(result),
                    () -> assertEquals(2, result.size()),
                    () -> assertEquals(expectedRecommendations, result)
            );

            verify(recommendationService, times(1)).getAllUserRecommendations(RECEIVER_ID);
        }

        @Test
        public void testGetAllUserRecommendationsEmptyList() {
            when(recommendationService.getAllUserRecommendations(RECEIVER_ID)).thenReturn(Collections.emptyList());

            List<RecommendationDto> result = recommendationController.getAllUserRecommendations(RECEIVER_ID);

            assertAll(
                    () -> assertNotNull(result),
                    () -> assertTrue(result.isEmpty())
            );

            verify(recommendationService, times(1)).getAllUserRecommendations(RECEIVER_ID);
        }

        @Test
        public void testGetAllUserRecommendationsServiceThrowsException() {
            when(recommendationService.getAllUserRecommendations(INVALID_ID))
                    .thenThrow(new RuntimeException("Invalid receiver ID"));

            Exception exception = assertThrows(RuntimeException.class, () -> {
                recommendationController.getAllUserRecommendations(INVALID_ID);
            });

            assertEquals("Invalid receiver ID", exception.getMessage());

            verify(recommendationService, times(1)).getAllUserRecommendations(INVALID_ID);
        }
    }

    @Nested
    class GetAllGivenRecommendationsTests {
        @Test
        public void testGetAllGivenRecommendationsSuccess() {
            List<RecommendationDto> expectedRecommendations = Arrays.asList(recommendation1, recommendation2);

            when(recommendationService.getAllGivenRecommendations(AUTHOR_ID)).thenReturn(expectedRecommendations);

            List<RecommendationDto> result = recommendationController.getAllGivenRecommendations(AUTHOR_ID);

            assertAll(
                    () -> assertNotNull(result),
                    () -> assertEquals(2, result.size()),
                    () -> assertEquals(expectedRecommendations, result)
            );

            verify(recommendationService, times(1)).getAllGivenRecommendations(AUTHOR_ID);
        }

        @Test
        public void testGetAllGivenRecommendationsEmptyList() {
            when(recommendationService.getAllGivenRecommendations(AUTHOR_ID)).thenReturn(Collections.emptyList());

            List<RecommendationDto> result = recommendationController.getAllGivenRecommendations(AUTHOR_ID);

            assertAll(
                    () -> assertNotNull(result),
                    () -> assertTrue(result.isEmpty())
            );

            verify(recommendationService, times(1)).getAllGivenRecommendations(AUTHOR_ID);
        }

        @Test
        public void testGetAllGivenRecommendationsServiceThrowsException() {
            when(recommendationService.getAllGivenRecommendations(INVALID_ID))
                    .thenThrow(new RuntimeException("Invalid author ID"));

            Exception exception = assertThrows(RuntimeException.class, () -> {
                recommendationController.getAllGivenRecommendations(INVALID_ID);
            });

            assertEquals("Invalid author ID", exception.getMessage());

            verify(recommendationService, times(1)).getAllGivenRecommendations(INVALID_ID);
        }
    }

    private RecommendationDto createRecommendationDto(Long id, String content) {
        RecommendationDto dto = new RecommendationDto();
        dto.setId(id);
        dto.setContent(content);
        return dto;
    }

}
