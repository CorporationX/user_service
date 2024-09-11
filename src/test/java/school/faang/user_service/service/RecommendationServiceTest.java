package school.faang.user_service.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.validator.RecommendationDtoValidator;
import school.faang.user_service.validator.SkillValidator;
import school.faang.user_service.validator.UserValidator;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {

    @InjectMocks
    private RecommendationService recommendationService;

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private RecommendationDtoValidator recommendationDtoValidator;

    @Mock
    private UserValidator userValidator;

    @Mock
    private SkillValidator skillValidator;

    @Mock
    private RecommendationMapper recommendationMapper;

    private static final long USER_ID = 1;

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("Успешное создание рекомендации")
        public void testCreateWithSaveRecommendation() {
            RecommendationDto recommendationDto = createRecommendationDto();
            Recommendation recommendation = getRecommendation();
            when(userValidator.checkIfUserIsExist(recommendationDto.getAuthorId())).thenReturn(getUser());
            when(userValidator.checkIfUserIsExist(recommendationDto.getReceiverId())).thenReturn(getUser());
            when(recommendationRepository
                    .create(recommendationDto.getAuthorId(), recommendationDto.getReceiverId(), recommendationDto.getContent()))
                    .thenReturn(recommendation.getId());
            when(recommendationRepository.findById(recommendation.getId())).thenReturn(Optional.of(recommendation));
            when(recommendationMapper.toDto(recommendation)).thenReturn(recommendationDto);

            RecommendationDto resultRecommendationDto = recommendationService.create(recommendationDto);

            assertEquals(recommendationDto, resultRecommendationDto);
            verify(recommendationDtoValidator, times(1))
                    .validateIfRecommendationContentIsBlank(recommendationDto);
            verify(recommendationDtoValidator, times(1))
                    .validateIfRecommendationCreatedTimeIsShort(recommendationDto);
            verify(userValidator, times(2))
                    .checkIfUserIsExist(USER_ID);
            verify(skillValidator, times(1)).getSkillsFromDb(recommendationDto);
            verify(recommendationRepository, times(1))
                    .create(recommendationDto.getAuthorId(), recommendationDto.getReceiverId(), recommendationDto.getContent());
            verify(recommendationMapper, times(1)).toDto(recommendation);
        }

        @Test
        @DisplayName("Успешное удаление рекомендации")
        public void testDelete() {
            recommendationService.delete(USER_ID);
            verify(recommendationRepository, times(1)).deleteById(USER_ID);
        }

        @Test
        @DisplayName("Успешное получение всех рекомендаций пользователя")
        public void testGetAllUserRecommendations() {
            List<Recommendation> recommendations = List.of(new Recommendation(), new Recommendation());
            List<RecommendationDto> recommendationDtos = List.of(new RecommendationDto(), new RecommendationDto());
            when(recommendationRepository.findAllByReceiverId(USER_ID, Pageable.unpaged()))
                    .thenReturn(new PageImpl<>(recommendations, Pageable.unpaged(), recommendations.size()));
            when(recommendationMapper.toDtos(recommendations)).thenReturn(recommendationDtos);

            List<RecommendationDto> resultRecommendationDtos = recommendationService.getAllUserRecommendations(USER_ID);

            assertEquals(recommendationDtos, resultRecommendationDtos);
            verify(recommendationRepository, times(1))
                    .findAllByReceiverId(USER_ID, Pageable.unpaged());
            verify(recommendationMapper, times(1)).toDtos(recommendations);
        }

        @Test
        @DisplayName("Успешное получение всех рекомендации автора")
        public void testGetAllGivenRecommendations() {
            List<Recommendation> recommendations = List.of(new Recommendation(), new Recommendation());
            List<RecommendationDto> recommendationDtos = List.of(new RecommendationDto(), new RecommendationDto());
            when(recommendationRepository.findAllByAuthorId(USER_ID, Pageable.unpaged()))
                    .thenReturn(new PageImpl<>(recommendations, Pageable.unpaged(), recommendations.size()));
            when(recommendationMapper.toDtos(recommendations)).thenReturn(recommendationDtos);

            List<RecommendationDto> resultRecommendationDtos = recommendationService.getAllGivenRecommendations(USER_ID);

            assertEquals(recommendationDtos, resultRecommendationDtos);
            verify(recommendationRepository, times(1))
                    .findAllByAuthorId(USER_ID, Pageable.unpaged());
            verify(recommendationMapper, times(1)).toDtos(recommendations);
        }
    }

    private RecommendationDto createRecommendationDto() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setContent("content");
        recommendationDto.setAuthorId(USER_ID);
        recommendationDto.setReceiverId(USER_ID);
        recommendationDto.setCreatedAt(LocalDateTime.now());

        return recommendationDto;
    }

    private Recommendation getRecommendation() {
        Recommendation recommendation = new Recommendation();
        recommendation.setId(10L);
        recommendation.setContent("content");
        recommendation.setAuthor(new User());
        recommendation.setReceiver(new User());
        recommendation.setSkillOffers(List.of(new SkillOffer()));
        recommendation.setRequest(new RecommendationRequest());
        recommendation.setCreatedAt(LocalDateTime.of(2014, Month.JULY, 2 , 15, 30));

        return recommendation;
    }

    private User getUser() {
        User user = new User();
        user.setId(USER_ID);
        return user;
    }
}
