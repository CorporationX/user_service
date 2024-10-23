package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.skill.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.service.skill.SkillOfferService;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.validator.recommendation.RecommendationDtoValidator;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {

    private static final long ID = 1L;
    private static final String CONTENT = "content";
    private static final String UPDATED_CONTENT = "update";
    @InjectMocks
    private RecommendationService recommendationService;
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private RecommendationDtoValidator recommendationDtoValidator;
    @Mock
    private SkillService skillService;
    @Mock
    private SkillOfferService skillOfferService;
    @Mock
    private RecommendationMapper recommendationMapper;
    private RecommendationDto recommendationDto;
    private RecommendationDto updatedRecommendationDto;
    private Recommendation recommendation;
    private List<Recommendation> recommendations;
    private List<RecommendationDto> recommendationDtos;
    private List<Skill> skills;

    @BeforeEach
    public void init() {
        recommendationDto = RecommendationDto.builder()
                .id(ID)
                .content(CONTENT)
                .authorId(ID)
                .receiverId(ID)
                .skillOffers(List.of(SkillOfferDto.builder()
                        .id(ID)
                        .build()))
                .createdAt(LocalDateTime.of(2014, Month.JULY, 2, 15, 30))
                .build();
        updatedRecommendationDto = RecommendationDto.builder()
                .id(ID)
                .content(UPDATED_CONTENT)
                .authorId(ID)
                .receiverId(ID)
                .skillOffers(List.of(SkillOfferDto.builder()
                        .id(ID)
                        .build()))
                .createdAt(LocalDateTime.of(2014, Month.JULY, 2, 15, 30))
                .build();

        skills = List.of(Skill.builder()
                .id(ID)
                .build());

        recommendation = Recommendation.builder()
                .id(ID)
                .content(CONTENT)
                .author(User.builder()
                        .id(ID)
                        .build())
                .receiver(User.builder()
                        .id(ID)
                        .build())
                .skillOffers(List.of(SkillOffer.builder()
                        .id(ID)
                        .build()))
                .createdAt(LocalDateTime.of(2014, Month.JULY, 2, 15, 30))
                .build();

        recommendations = List.of(new Recommendation(), new Recommendation());
        recommendationDtos = List.of(RecommendationDto.builder().build(), RecommendationDto.builder().build());
    }

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("Успешное создание рекомендации")
        public void whenCreateRecommendationThenSuccess() {
            when(skillService.getSkillByIds(anyList())).thenReturn(skills);
            when(recommendationRepository.create(recommendationDto.getAuthorId(),
                    recommendationDto.getReceiverId(), recommendationDto.getContent())).thenReturn(ID);
            when(recommendationRepository.findById(ID)).thenReturn(Optional.ofNullable(recommendation));
            when(recommendationMapper.toDto(recommendation)).thenReturn(recommendationDto);

            RecommendationDto resultRecommendationDto = recommendationService.create(recommendationDto);

            assertNotNull(resultRecommendationDto);
            verify(recommendationDtoValidator).validateRecommendation(recommendationDto);
            verify(skillService).getSkillByIds(anyList());
            verify(recommendationRepository).create(recommendationDto.getAuthorId(),
                    recommendationDto.getReceiverId(), recommendationDto.getContent());
            verify(recommendationRepository).findById(ID);
            verify(skillOfferService).addSkillsWithGuarantees(skills, recommendation.getId(), resultRecommendationDto);
            verify(recommendationMapper).toDto(recommendation);
        }

        @Test
        @DisplayName("Успешное обновление рекомендации")
        public void whenUpdateRecommendationThenSuccess() {
            when(skillService.getSkillByIds(anyList())).thenReturn(skills);
            when(recommendationRepository.findById(ID)).thenReturn(Optional.ofNullable(recommendation));
            when(recommendationMapper.toDto(recommendation)).thenReturn(updatedRecommendationDto);

            RecommendationDto resultRecommendationDto = recommendationService.update(ID, updatedRecommendationDto);

            assertNotNull(resultRecommendationDto);
            assertEquals(updatedRecommendationDto.getContent(), resultRecommendationDto.getContent());
            verify(recommendationDtoValidator).validateRecommendation(updatedRecommendationDto);
            verify(skillService).getSkillByIds(anyList());
            verify(recommendationRepository).update(updatedRecommendationDto.getAuthorId(),
                    updatedRecommendationDto.getReceiverId(), updatedRecommendationDto.getContent());
            verify(recommendationRepository).findById(ID);
            verify(skillOfferService).deleteAllByRecommendationId(recommendation.getId());
            verify(skillOfferService)
                    .addSkillsWithGuarantees(skills, recommendation.getId(), resultRecommendationDto);
            verify(recommendationMapper).toDto(recommendation);
        }

        @Test
        @DisplayName("Успешное удаление рекомендации")
        public void whenDeleteThenSuccess() {
            recommendationService.delete(ID);
            verify(recommendationRepository).deleteById(ID);
        }

        @Test
        @DisplayName("Успешное получение всех рекомендаций пользователя")
        public void whenGetAllUserRecommendationsThenSuccess() {
            when(recommendationRepository.findAllByReceiverId(ID, Pageable.unpaged()))
                    .thenReturn(new PageImpl<>(recommendations, Pageable.unpaged(), recommendations.size()));
            when(recommendationMapper.toDtos(recommendations)).thenReturn(recommendationDtos);

            List<RecommendationDto> resultRecommendationDtos = recommendationService.getAllUserRecommendations(ID);

            assertEquals(recommendationDtos, resultRecommendationDtos);
            verify(recommendationRepository).findAllByReceiverId(ID, Pageable.unpaged());
            verify(recommendationMapper).toDtos(recommendations);
        }

        @Test
        @DisplayName("Успешное получение всех рекомендации автора")
        public void whenGetAllGivenRecommendationsThenSuccess() {
            when(recommendationRepository.findAllByAuthorId(ID, Pageable.unpaged()))
                    .thenReturn(new PageImpl<>(recommendations, Pageable.unpaged(), recommendations.size()));
            when(recommendationMapper.toDtos(recommendations)).thenReturn(recommendationDtos);

            List<RecommendationDto> resultRecommendationDtos = recommendationService.getAllGivenRecommendations(ID);

            assertEquals(recommendationDtos, resultRecommendationDtos);
            verify(recommendationRepository).findAllByAuthorId(ID, Pageable.unpaged());
            verify(recommendationMapper).toDtos(recommendations);
        }
    }
}
