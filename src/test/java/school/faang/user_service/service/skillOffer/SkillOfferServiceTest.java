package school.faang.user_service.service.skillOffer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recomendation.SkillOfferDto;
import school.faang.user_service.dto.skill.SkillOfferedEventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.SkillOfferMapper;
import school.faang.user_service.publisher.skillOffer.SkillOfferPublisher;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.skill.SkillOfferValidator;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillOfferServiceTest {
    @Mock
    SkillOfferRepository skillOfferRepository;

    @Mock
    SkillRepository skillRepository;

    @Mock
    RecommendationRepository recommendationRepository;

    @Mock
    SkillOfferValidator skillOfferValidator;

    @Mock
    SkillOfferMapper skillOfferMapper;

    @Mock
    SkillOfferPublisher skillOfferPublisher;

    @InjectMocks
    SkillOfferService skillOfferService;

    Long senderId;
    Long receiverId;
    Long skillId;
    Skill skill;
    Recommendation recommendation;
    SkillOffer skillOffer;
    SkillOfferDto skillOfferDto;
    SkillOfferedEventDto skillOfferedEventDto;

    @BeforeEach
    void setUp() {
        senderId = 1L;
        receiverId = 1L;
        skillId = 1L;
        skill = new Skill();
        recommendation = new Recommendation();
        skillOffer = new SkillOffer();
        skillOfferDto = SkillOfferDto.builder().build();
        skillOfferedEventDto = SkillOfferedEventDto.builder()
            .senderId(senderId)
            .receiverId(receiverId)
            .skillId(skillId)
            .build();
    }

    @Test
    @DisplayName("Should successfully offer skill to user and return SkillOfferDto")
    void offerSkillToUser() {
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(senderId, receiverId))
            .thenReturn(Optional.of(recommendation));
        when(skillOfferRepository.save(any(SkillOffer.class))).thenReturn(skillOffer);
        when(skillOfferMapper.toDto(skillOffer)).thenReturn(skillOfferDto);

        SkillOfferDto result = skillOfferService.offerSkillToUser(senderId, receiverId, skillId);

        verify(skillOfferValidator).validateSkillOffer(senderId, receiverId, skillId);
        verify(skillOfferPublisher).publish(skillOfferedEventDto);
        verify(skillRepository).findById(skillId);
        verify(recommendationRepository).findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(senderId, receiverId);
        verify(skillOfferRepository).save(any(SkillOffer.class));
        verify(skillOfferMapper).toDto(skillOffer);
        assertNotNull(result);
        assertEquals(skillOfferDto, result);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when skill is not found")
    void offerSkillToUser_SkillNotFound_ThrowException() {
        when(skillRepository.findById(skillId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> skillOfferService.offerSkillToUser(senderId, receiverId, skillId));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when recommendation is not found")
    void offerSkillToUser_RecommendationNotFound_ThrowException() {
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(senderId, receiverId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> skillOfferService.offerSkillToUser(senderId, receiverId, skillId));
    }
}