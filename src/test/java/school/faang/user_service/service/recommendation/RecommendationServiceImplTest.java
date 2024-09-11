package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.dto.SkillOfferDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.impl.RecommendationServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceImplTest {

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Spy
    private RecommendationMapper recommendationMapper = Mappers.getMapper(RecommendationMapper.class);

    @InjectMocks
    private RecommendationServiceImpl recommendationService;

    private RecommendationDto recommendationDto;
    private User author;
    private User receiver;

    @BeforeEach
    void setUp() {
        recommendationDto = new RecommendationDto(
                1L, 1L, 2L, "Test Recommendation", List.of(new SkillOfferDto(1L, 1L, 1L)), null);

        author = User.builder().id(1L).build();
        receiver = User.builder().id(2L).build();
    }

    @Test
    @DisplayName("Создание рекомендации с несуществующим автором")
    void testCreateRecommendationWithInvalidAuthor() {
        when(userRepository.findById(recommendationDto.authorId())).thenReturn(Optional.empty());
        when(userRepository.findById(recommendationDto.receiverId())).thenReturn(Optional.of(receiver));

        assertThrows(DataValidationException.class,
                () -> recommendationService.create(recommendationDto));

        verify(userRepository).findById(recommendationDto.authorId());
        verify(userRepository).findById(recommendationDto.receiverId());
        verifyNoMoreInteractions(recommendationRepository, skillOfferRepository);
    }


    @Test
    @DisplayName("Создание рекомендации с несуществующим получателем")
    void testCreateRecommendationWithInvalidReceiver() {
        when(userRepository.findById(recommendationDto.authorId())).thenReturn(Optional.of(author));
        when(userRepository.findById(recommendationDto.receiverId())).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class,
                () -> recommendationService.create(recommendationDto));

        verify(userRepository).findById(recommendationDto.authorId());
        verify(userRepository).findById(recommendationDto.receiverId());
        verifyNoMoreInteractions(recommendationRepository, skillOfferRepository);
    }

    @Test
    @DisplayName("Обновление несуществующей рекомендации")
    void testUpdateNonExistingRecommendation() {
        long recommendationId = 99L;
        when(recommendationRepository.findById(recommendationId)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class,
                () -> recommendationService.update(recommendationId, recommendationDto));

        verify(recommendationRepository).findById(recommendationId);
        verifyNoMoreInteractions(skillOfferRepository);
    }

    @Test
    @DisplayName("Удаление несуществующей рекомендации")
    void testDeleteNonExistingRecommendation() {
        long recommendationId = 99L;
        when(recommendationRepository.findById(recommendationId)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class,
                () -> recommendationService.delete(recommendationId));

        verify(recommendationRepository).findById(recommendationId);
        verifyNoMoreInteractions(recommendationRepository);
    }

    @Test
    @DisplayName("Позитивный тест на создание рекомендации")
    void testCreateRecommendationSuccess() {
        when(userRepository.findById(recommendationDto.authorId())).thenReturn(Optional.of(author));
        when(userRepository.findById(recommendationDto.receiverId())).thenReturn(Optional.of(receiver));

        recommendationService.create(recommendationDto);

        verify(userRepository).findById(recommendationDto.authorId());
        verify(userRepository).findById(recommendationDto.receiverId());
        verify(recommendationRepository).save(any(Recommendation.class));
        verify(skillOfferRepository, times(1)).save(any());
    }
}