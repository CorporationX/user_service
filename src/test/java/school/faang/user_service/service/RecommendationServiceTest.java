package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import school.faang.user_service.model.dto.RecommendationDto;
import school.faang.user_service.model.dto.SkillOfferDto;
import school.faang.user_service.model.entity.Skill;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.model.entity.UserSkillGuarantee;
import school.faang.user_service.model.entity.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.publisher.RecommendationReceivedEventPublisher;
import school.faang.user_service.model.event.SkillOfferedEvent;
import school.faang.user_service.publisher.SkillOfferedEventPublisher;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.RecommendationRepository;
import school.faang.user_service.repository.SkillOfferRepository;
import school.faang.user_service.service.impl.RecommendationServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RecommendationMapper recommendationMapper;

    @Mock
    private RecommendationReceivedEventPublisher recommendationReceivedEventPublisher;

    @Mock
    private SkillOfferedEventPublisher skillOfferedEventPublisher;

    @InjectMocks
    private RecommendationServiceImpl recommendationService;

    @Test
    void testCreate_WhenValidData_SkillOffersNotNull() {
        Long authorId = 1L;
        Long receiverId = 2L;
        Long anotherGuarantorId = 3L;
        long skillId = 3L;
        long sixMonthAgoRecommendationId = 1L;
        Long currentRecommendationId = 2L;
        String content = "Test content";

        Recommendation recommendation = Recommendation.builder()
                .id(currentRecommendationId)
                .content(content)
                .build();

        RecommendationDto dto = new RecommendationDto();
        dto.setContent(content);
        dto.setAuthorId(authorId);
        dto.setReceiverId(receiverId);
        dto.setSkillOffers(List.of(new SkillOfferDto(1L, skillId, authorId, receiverId)));
        when(skillRepository.countExisting(any())).thenReturn(dto.getSkillOffers().size());
        when(recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, receiverId))
                .thenReturn(Optional.of(Recommendation.builder()
                        .id(sixMonthAgoRecommendationId)
                        .author(User.builder().id(authorId).build())
                        .receiver(User.builder().id(receiverId).build())
                        .createdAt(LocalDateTime.now().minusMonths(6).minusDays(1))
                        .build()));
        when(recommendationRepository.create(eq(authorId), eq(receiverId), anyString())).thenReturn(currentRecommendationId);
        List<UserSkillGuarantee> guarantees = new ArrayList<>();
        guarantees.add(UserSkillGuarantee.builder()
                .guarantor(User.builder()
                        .id(anotherGuarantorId)
                        .build())
                .build());
        when(skillRepository.findUserSkill(skillId, receiverId)).thenReturn(Optional.of(Skill.builder()
                .guarantees(guarantees).build()));
        when(userRepository.findById(receiverId)).thenReturn(Optional.of(User.builder()
                .id(receiverId)
                .build()));
        when(userRepository.findById(authorId)).thenReturn(Optional.of(User.builder()
                .id(authorId)
                .build()));
        when(recommendationRepository.findById(currentRecommendationId)).thenReturn(Optional.of(recommendation));
        when(recommendationMapper.toDto(any(Recommendation.class))).thenReturn(dto);

        RecommendationDto result = recommendationService.create(dto);

        verify(skillRepository, times(1)).countExisting(any());
        verify(recommendationRepository, times(1)).findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, receiverId);
        verify(recommendationRepository, times(1)).create(anyLong(), anyLong(), anyString());
        verify(skillOfferRepository, times(1)).create(anyLong(), anyLong());
        verify(skillRepository, times(1)).findUserSkill(skillId, receiverId);
        verify(userRepository, times(1)).findById(receiverId);
        verify(userRepository, times(1)).findById(authorId);
        verify(skillRepository, times(1)).save(any());
        assertNotNull(result);
        assertEquals("Test content", result.getContent());

        ArgumentCaptor<SkillOfferedEvent> captor = ArgumentCaptor.forClass(SkillOfferedEvent.class);
        verify(skillOfferedEventPublisher, times(1)).publish(captor.capture());
        SkillOfferedEvent capturedEvent = captor.getValue();
        assertEquals(skillId, capturedEvent.getSkillId());
        assertEquals(authorId, capturedEvent.getSenderId());
        assertEquals(receiverId, capturedEvent.getReceiverId());
    }

    @Test
    void testCreate_ThrowsDataValidationException_WhenNotAllSkillsExist() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setAuthorId(1L);
        recommendationDto.setReceiverId(2L);
        recommendationDto.setContent("Some content");
        recommendationDto.setSkillOffers(List.of(new SkillOfferDto(1L, 10L, 1L, 2L)));

        when(skillRepository.countExisting(any())).thenReturn(0);

        assertThrows(DataValidationException.class, () -> recommendationService.create(recommendationDto));

        verify(skillRepository).countExisting(any());
        verifyNoMoreInteractions(recommendationRepository);
    }

    @Test
    void testCreate_ThrowsDataValidationException_WhenLastRecommendationTooRecent() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setAuthorId(1L);
        recommendationDto.setReceiverId(2L);
        recommendationDto.setContent("Test content");

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(anyLong(), anyLong()))
                .thenReturn(Optional.of(Recommendation.builder()
                        .createdAt(LocalDateTime.now().minusMonths(3))
                        .build()));

        assertThrows(DataValidationException.class, () -> recommendationService.create(recommendationDto));

        verify(recommendationRepository).findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(anyLong(), anyLong());
        verifyNoInteractions(skillOfferRepository);
    }

    @Test
    void testCreate_ThrowsNoSuchElementException_WhenUserNotFound() {
        Long authorId = 1L;
        Long receiverId = 2L;
        Long anotherGuarantorId = 3L;
        long skillId = 3L;
        long sixMonthAgoRecommendationId = 1L;
        long currentRecommendationId = 2L;
        String content = "Test content";

        RecommendationDto dto = new RecommendationDto();
        dto.setContent(content);
        dto.setAuthorId(authorId);
        dto.setReceiverId(receiverId);
        dto.setSkillOffers(List.of(new SkillOfferDto(1L, skillId, authorId, receiverId)));
        when(skillRepository.countExisting(any())).thenReturn(dto.getSkillOffers().size());
        when(recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, receiverId))
                .thenReturn(Optional.of(Recommendation.builder()
                        .id(sixMonthAgoRecommendationId)
                        .author(User.builder().id(authorId).build())
                        .receiver(User.builder().id(receiverId).build())
                        .createdAt(LocalDateTime.now().minusMonths(6).minusDays(1))
                        .build()));
        when(recommendationRepository.create(eq(authorId), eq(receiverId), anyString())).thenReturn(currentRecommendationId);
        List<UserSkillGuarantee> guarantees = new ArrayList<>();
        guarantees.add(UserSkillGuarantee.builder()
                .guarantor(User.builder()
                        .id(anotherGuarantorId)
                        .build())
                .build());
        when(skillRepository.findUserSkill(skillId, receiverId)).thenReturn(Optional.of(Skill.builder()
                .guarantees(guarantees).build()));
        when(userRepository.findById(anyLong())).thenThrow(new NoSuchElementException("User not found"));

        assertThrows(NoSuchElementException.class, () -> recommendationService.create(dto));

        verifyNoMoreInteractions(skillRepository);
    }

    @Test
    void testUpdate_ValidData_SkillOfferNotNull() {
        Long authorId = 1L;
        Long receiverId = 2L;
        Long anotherGuarantorId = 3L;
        long skillId = 3L;
        long sixMonthAgoRecommendationId = 1L;
        long currentRecommendationId = 2L;

        RecommendationDto inputDto = new RecommendationDto();
        inputDto.setId(currentRecommendationId);
        inputDto.setContent("Updated content");
        inputDto.setAuthorId(authorId);
        inputDto.setReceiverId(receiverId);
        inputDto.setSkillOffers(List.of(new SkillOfferDto(1L, skillId, authorId, receiverId)));

        Recommendation existingRecommendation = new Recommendation();
        existingRecommendation.setId(currentRecommendationId);
        existingRecommendation.setContent("Old content");

        Recommendation updatedRecommendation = new Recommendation();
        updatedRecommendation.setId(currentRecommendationId);
        updatedRecommendation.setContent("Updated content");

        when(skillRepository.countExisting(any())).thenReturn(inputDto.getSkillOffers().size());
        when(recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, receiverId))
                .thenReturn(Optional.of(Recommendation.builder()
                        .id(sixMonthAgoRecommendationId)
                        .author(User.builder().id(authorId).build())
                        .receiver(User.builder().id(receiverId).build())
                        .createdAt(LocalDateTime.now().minusMonths(6).minusDays(1))
                        .build()));
        List<UserSkillGuarantee> guarantees = new ArrayList<>();
        guarantees.add(UserSkillGuarantee.builder()
                .guarantor(User.builder()
                        .id(anotherGuarantorId)
                        .build())
                .build());
        when(skillRepository.findUserSkill(skillId, receiverId)).thenReturn(Optional.of(Skill.builder()
                .guarantees(guarantees).build()));
        when(userRepository.findById(receiverId)).thenReturn(Optional.of(User.builder()
                .id(receiverId)
                .build()));
        when(userRepository.findById(authorId)).thenReturn(Optional.of(User.builder()
                .id(authorId)
                .build()));
        when(recommendationRepository.findById(currentRecommendationId)).thenReturn(Optional.of(existingRecommendation));
        when(recommendationMapper.toDto(any(Recommendation.class))).thenReturn(inputDto);

        RecommendationDto result = recommendationService.update(currentRecommendationId, inputDto);

        assertNotNull(result);
        assertEquals("Updated content", result.getContent());
        verify(skillRepository, times(1)).countExisting(any());
        verify(recommendationRepository, times(1)).findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, receiverId);
        verify(recommendationRepository, times(1)).update(anyLong(), anyLong(), anyString());
        verify(skillOfferRepository, times(1)).deleteAllByRecommendationId(currentRecommendationId);
        verify(skillOfferRepository, times(1)).create(anyLong(), anyLong());
        verify(skillRepository, times(1)).findUserSkill(skillId, receiverId);
        verify(userRepository, times(1)).findById(receiverId);
        verify(userRepository, times(1)).findById(authorId);
        verify(skillRepository, times(1)).save(any());

        ArgumentCaptor<SkillOfferedEvent> captor = ArgumentCaptor.forClass(SkillOfferedEvent.class);
        verify(skillOfferedEventPublisher, times(1)).publish(captor.capture());
        SkillOfferedEvent capturedEvent = captor.getValue();
        assertEquals(skillId, capturedEvent.getSkillId());
        assertEquals(authorId, capturedEvent.getSenderId());
        assertEquals(receiverId, capturedEvent.getReceiverId());
    }

    @Test
    void testCreate_ThrowsDataValidationException_WhenIdsDontMatch() {
        long idFromPath = 1L;

        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setId(2L);
        recommendationDto.setContent("Content");

        assertThrows(DataValidationException.class, () -> recommendationService.update(idFromPath, recommendationDto));
        verifyNoInteractions(skillRepository);
    }

    @Test
    void testGetAllUserRecommendations_ValidData() {
        long userId = 1L;
        String content1 = "Content 1";
        String content2 = "Content 2";
        Recommendation recommendation1 = Recommendation.builder()
                .id(1L)
                .content(content1)
                .build();
        Recommendation recommendation2 = Recommendation.builder()
                .id(2L)
                .content(content2)
                .build();
        List<Recommendation> recommendations = Arrays.asList(recommendation1, recommendation2);

        RecommendationDto dto1 = new RecommendationDto();
        dto1.setId(1L);
        dto1.setContent(content1);
        RecommendationDto dto2 = new RecommendationDto();
        dto2.setId(2L);
        dto2.setContent(content2);
        List<RecommendationDto> recommendationDtoList = List.of(dto1, dto2);

        when(recommendationRepository.findAllByReceiverId(userId, Pageable.unpaged())).thenReturn(new PageImpl<>(recommendations));
        when(recommendationMapper.toDtoList(recommendations)).thenReturn(recommendationDtoList);

        List<RecommendationDto> result = recommendationService.getAllUserRecommendations(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(content1, result.get(0).getContent());
        assertEquals(content2, result.get(1).getContent());

        verify(recommendationRepository).findAllByReceiverId(userId, Pageable.unpaged());
        verify(recommendationMapper).toDtoList(recommendations);
    }

    @Test
    void delete_CallsDeleteOnRepository_WhenCalled() {
        recommendationService.delete(1L);
        verify(recommendationRepository, times(1)).deleteById(1L);
    }
}
