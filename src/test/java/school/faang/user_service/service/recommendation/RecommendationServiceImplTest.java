package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.recommendation.CreateRecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationEventDto;
import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.dto.recommendation.UpdateRecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.messages.kafka.producers.RecommendationPublisher;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceImplTest {

    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserContext userContext;
    @Mock
    private RecommendationPublisher recommendationPublisher;
    @InjectMocks
    private RecommendationServiceImpl service;

    @BeforeEach
    public void setup() {
        try {
            var f = RecommendationServiceImpl.class.getDeclaredField("limit");
            f.setAccessible(true);
            f.setInt(service, 6);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("create: успех")
    public void create_success() {
        given(userContext.getUserId()).willReturn(1L);
        given(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(1L, 2L))
                .willReturn(Optional.empty());

        User author = new User();
        author.setId(1L);
        User receiver = new User();
        receiver.setId(2L);
        given(userRepository.getByIdOrThrow(1L)).willReturn(author);
        given(userRepository.getByIdOrThrow(2L)).willReturn(receiver);

        Recommendation saved = new Recommendation();
        saved.setId(100L);
        saved.setAuthor(author);
        saved.setReceiver(receiver);
        saved.setContent("hi");
        given(recommendationRepository.save(any(Recommendation.class))).willReturn(saved);

        var dto = service.create(new CreateRecommendationDto(2L, "hi"));

        assertThat(dto.getId()).isEqualTo(100L);
        assertThat(dto.getAuthorId()).isEqualTo(1L);
        assertThat(dto.getReceiverId()).isEqualTo(2L);
        assertThat(dto.getContent()).isEqualTo("hi");
        verify(recommendationRepository).save(any(Recommendation.class));
    }

    @Test
    @DisplayName("create: self-recommendation — ошибка")
    public void create_selfRecommendation_error() {
        given(userContext.getUserId()).willReturn(2L);

        assertThatThrownBy(() -> service.create(new CreateRecommendationDto(2L, "hi")))
                .isInstanceOf(DataValidationException.class);
    }

    @Test
    @DisplayName("create: отсутствует пользователь — ошибка")
    public void create_userNotFound_error() {
        given(userContext.getUserId()).willReturn(1L);

        User author = new User();
        author.setId(1L);
        given(userRepository.getByIdOrThrow(1L)).willReturn(author);
        given(userRepository.getByIdOrThrow(2L))
                .willThrow(new EntityNotFoundException("User not found"));

        assertThatThrownBy(() -> service.create(new CreateRecommendationDto(2L, "hi")))
                .isInstanceOf(EntityNotFoundException.class);
        verify(recommendationRepository, never()).save(any());
    }

    @Test
    @DisplayName("create: частые рекомендации — ошибка")
    public void create_tooOften_error() {
        given(userContext.getUserId()).willReturn(1L);
        Recommendation recent = new Recommendation();
        recent.setCreatedAt(LocalDateTime.now());
        given(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(1L, 2L))
                .willReturn(Optional.of(recent));

        assertThatThrownBy(() -> service.create(new CreateRecommendationDto(2L, "hi")))
                .isInstanceOf(DataValidationException.class);
    }

    @Test
    public void create_shouldPublishToTopic() {
        given(userContext.getUserId()).willReturn(1L);
        given(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(1L, 2L))
                .willReturn(Optional.empty());

        User author = new User();
        author.setId(1L);
        User receiver = new User();
        receiver.setId(2L);
        given(userRepository.getByIdOrThrow(1L)).willReturn(author);
        given(userRepository.getByIdOrThrow(2L)).willReturn(receiver);

        Recommendation saved = new Recommendation();
        saved.setId(100L);
        saved.setAuthor(author);
        saved.setReceiver(receiver);
        saved.setContent("hi");
        given(recommendationRepository.save(any(Recommendation.class))).willReturn(saved);

        service.create(new CreateRecommendationDto(2L, "Test"));

        RecommendationEventDto publishDto = RecommendationEventDto.builder()
                .authorId(saved.getAuthor().getId())
                .receiverId(saved.getReceiver().getId())
                .content(saved.getContent())
                .build();

        verify(recommendationPublisher).publish(publishDto);
    }

    @Test
    @DisplayName("update: успех")
    public void update_success() {
        given(userContext.getUserId()).willReturn(1L);
        Recommendation rec = new Recommendation();
        User author = new User();
        author.setId(1L);
        User receiver = new User();
        receiver.setId(2L);
        rec.setAuthor(author);
        rec.setReceiver(receiver);
        given(recommendationRepository.findById(10L)).willReturn(Optional.of(rec));

        var dto = service.update(new UpdateRecommendationDto("updated"), 10L);

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getAuthorId()).isEqualTo(1L);
        assertThat(dto.getReceiverId()).isEqualTo(2L);
        assertThat(dto.getContent()).isEqualTo("updated");
    }

    @Test
    @DisplayName("update: не автор — ошибка доступа")
    public void update_forbidden_error() {
        given(userContext.getUserId()).willReturn(9L);
        Recommendation rec = new Recommendation();
        User author = new User();
        author.setId(1L);
        rec.setAuthor(author);
        given(recommendationRepository.findById(10L)).willReturn(Optional.of(rec));

        assertThatThrownBy(() -> service.update(new UpdateRecommendationDto("updated"), 10L))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("update: не найдено — ошибка")
    public void update_notFound_error() {
        given(recommendationRepository.findById(10L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(new UpdateRecommendationDto("updated"), 10L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("delete: успех")
    public void delete_success() {
        given(userContext.getUserId()).willReturn(1L);
        given(recommendationRepository.findAuthorIdById(10L)).willReturn(Optional.of(1L));

        service.delete(10L);

        verify(recommendationRepository).deleteByIdAndAuthor_id(10L, 1L);
    }

    @Test
    @DisplayName("delete: не автор — ошибка доступа")
    public void delete_forbidden_error() {
        given(userContext.getUserId()).willReturn(2L);
        given(recommendationRepository.findAuthorIdById(10L)).willReturn(Optional.of(1L));

        assertThatThrownBy(() -> service.delete(10L))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("delete: не найдено — ошибка")
    public void delete_notFound_error() {
        given(recommendationRepository.findAuthorIdById(10L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(10L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("getByFilters: успех")
    public void getByFilters_success() {
        Recommendation recommendation = new Recommendation();
        recommendation.setId(1L);
        User author = new User();
        author.setId(1L);
        User receiver = new User();
        receiver.setId(2L);
        recommendation.setAuthor(author);
        recommendation.setReceiver(receiver);
        recommendation.setContent("c1");

        Pageable pageable = PageRequest.of(0, 10);
        Page<Recommendation> repositoryPage = new PageImpl<>(List.of(recommendation), pageable, 1);

        given(recommendationRepository.findByFilters("c", 2L, 1L, pageable))
                .willReturn(repositoryPage);

        RecommendationFilterDto filters = new RecommendationFilterDto("c", 1L, 2L);
        var result = service.getByFilters(filters, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
        assertThat(result.getContent().get(0).getContent()).isEqualTo("c1");
    }
}