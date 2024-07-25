package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static school.faang.user_service.exception.recommendation.RecommendationError.RECOMMENDATION_EXPIRATION_TIME_NOT_PASSED;
import static school.faang.user_service.exception.recommendation.RecommendationError.SKILL_IS_NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {
    @Spy
    RecommendationMapper recommendationMapper;
    @InjectMocks
    RecommendationService recommendationService;

    @Mock
    RecommendationRepository recommendationRepository;
    @Mock
    SkillOfferRepository skillOfferRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    SkillRepository skillRepository;
    @Mock
    UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    private RecommendationDto recommendationDto;
    private Recommendation recommendation;
    private User author;
    private User receiver;
    private Skill skill;
    private SkillOffer skillOffer;

    @BeforeEach
    void setUp() {
        author = new User();
        author.setId(1L);
        receiver = new User();
        receiver.setId(2L);

        skill = new Skill();
        skill.setId(1L);

        SkillOfferDto skillOfferDto = SkillOfferDto.builder()
                .recommendationId(1L)
                .skillId(1L)
                .build();

        recommendationDto = RecommendationDto.builder()
                .authorId(1L)
                .receiverId(2L)
                .skillOffers(List.of(skillOfferDto))
                .build();

        recommendation = new Recommendation();
        recommendation.setAuthor(author);
        recommendation.setReceiver(receiver);
        recommendation.setId(1L);

        skillOffer = new SkillOffer();
        skillOffer.setId(1L);
        skillOffer.setSkill(skill);

        recommendation.setSkillOffers(List.of(skillOffer));
    }

    @Test
    void create_ShouldReturnRecommendationDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));

        when(skillRepository.existsById(anyLong())).thenReturn(true);

        when(recommendationMapper.toEntity(recommendationDto)).thenReturn(recommendation);
        when(recommendationMapper.toDto(recommendation)).thenReturn(recommendationDto);

        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(recommendation);

        RecommendationDto result = recommendationService.create(recommendationDto);

        assertEquals(recommendationDto, result);
        verify(recommendationRepository, times(1)).save(any(Recommendation.class));
    }

    @Test
    void update_ShouldReturnRecommendationDto() {
        when(recommendationMapper.toEntity(any(RecommendationDto.class))).thenReturn(recommendation);
        when(recommendationMapper.toDto(any(Recommendation.class))).thenReturn(recommendationDto);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(author)).thenReturn(Optional.of(receiver));
        when(skillRepository.existsById(anyLong())).thenReturn(true);

        RecommendationDto updatedRecommendation = recommendationService.update(recommendationDto);

        assertNotNull(updatedRecommendation);
        verify(recommendationRepository, times(1)).save(any(Recommendation.class));
        verify(skillOfferRepository, times(1)).deleteAllByRecommendationId(anyLong());
    }

    @Test
    void testDeleteRecommendation() {
        when(recommendationRepository.findById(anyLong())).thenReturn(Optional.of(recommendation));

        recommendationService.delete(1L);

        verify(recommendationRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGetAllUserRecommendations() {
        List<Recommendation> recommendations = List.of(recommendation);
        Page<Recommendation> page = new PageImpl<>(recommendations);
        when(recommendationRepository.findAllByReceiverId(anyLong(), any(Pageable.class))).thenReturn(page);
        when(recommendationMapper.toDto(any(Recommendation.class))).thenReturn(recommendationDto);

        List<RecommendationDto> result = recommendationService.getAllUserRecommendations(2L, 0, 10);

        assertEquals(1, result.size());
        verify(recommendationRepository, times(1)).findAllByReceiverId(anyLong(), any(Pageable.class));
    }

    @Test
    void testGetAllGivenRecommendations() {
        List<Recommendation> recommendations = List.of(recommendation);
        Page<Recommendation> page = new PageImpl<>(recommendations);
        when(recommendationRepository.findAllByAuthorId(anyLong(), any(Pageable.class))).thenReturn(page);
        when(recommendationMapper.toDto(any(Recommendation.class))).thenReturn(recommendationDto);

        List<RecommendationDto> result = recommendationService.getAllGivenRecommendations(1L, 0, 10);

        assertEquals(1, result.size());
        verify(recommendationRepository, times(1)).findAllByAuthorId(anyLong(), any(Pageable.class));
    }

    @Test
    void processSkillAndGuarantees_ShouldCallAddNewGuarantee_WhenSkillIsPresentAndGuaranteeDoesNotExist() {
        when(skillRepository.findAllByUserId(receiver.getId())).thenReturn(List.of(skill));
        when(userSkillGuaranteeRepository.existsById(author.getId())).thenReturn(false);
        when(skillRepository.findById(skill.getId())).thenReturn(Optional.of(skill));

        recommendationService.processSkillAndGuarantees(recommendation);

        verify(userSkillGuaranteeRepository, times(1)).save(any(UserSkillGuarantee.class));
    }

    @Test
    void processSkillAndGuarantees_ShouldNotCallAddNewGuarantee_WhenSkillIsPresentButGuaranteeExists() {
        when(skillRepository.findAllByUserId(receiver.getId())).thenReturn(List.of(skill));
        when(userSkillGuaranteeRepository.existsById(author.getId())).thenReturn(true);

        recommendationService.processSkillAndGuarantees(recommendation);

        verify(userSkillGuaranteeRepository, times(0)).save(any(UserSkillGuarantee.class));
        verify(skillOfferRepository, times(1)).save(skillOffer);
    }

    @Test
    void processSkillAndGuarantees_ShouldSaveSkillOffer_WhenSkillIsNotPresent() {
        when(skillRepository.findAllByUserId(receiver.getId())).thenReturn(List.of());
//        when(userSkillGuaranteeRepository.existsById(author.getId())).thenReturn(false);

        recommendationService.processSkillAndGuarantees(recommendation);

        verify(skillOfferRepository, times(1)).save(skillOffer);
        verify(userSkillGuaranteeRepository, times(0)).save(any(UserSkillGuarantee.class));
    }

    @Test
    void addNewGuarantee_ShouldSaveUserSkillGuarantee() {
        when(skillRepository.findById(skill.getId())).thenReturn(Optional.of(skill));

        recommendationService.addNewGuarantee(author, receiver, skill.getId());

        ArgumentCaptor<UserSkillGuarantee> captor = ArgumentCaptor.forClass(UserSkillGuarantee.class);
        verify(userSkillGuaranteeRepository, times(1)).save(captor.capture());
        UserSkillGuarantee capturedGuarantee = captor.getValue();
        assertNotNull(capturedGuarantee);
        assertEquals(author, capturedGuarantee.getGuarantor());
        assertEquals(receiver, capturedGuarantee.getUser());
        assertEquals(skill, capturedGuarantee.getSkill());
    }

    @Test
    void validateIntervalAndSkill_ShouldThrowDataValidationException_WhenIntervalNotPassed() {
        LocalDateTime recentDate = LocalDateTime.now().minusMonths(1);
        Recommendation recentRecommendation = new Recommendation();
        recentRecommendation.setCreatedAt(recentDate);
        recentRecommendation.setReceiver(receiver);
        author.setRecommendationsGiven(List.of(recentRecommendation));

        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));


        DataValidationException exception = assertThrows(
                DataValidationException.class, () -> recommendationService.create(recommendationDto)
        );

        assertEquals(RECOMMENDATION_EXPIRATION_TIME_NOT_PASSED.getMessage(), exception.getMessage());
    }

    @Test
    void validateIntervalAndSkill_ShouldThrowDataValidateException_WhenSkillNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(skillRepository.existsById(anyLong())).thenReturn(false);

        DataValidationException exception = assertThrows(DataValidationException.class,

                () -> recommendationService.create(recommendationDto));

        assertEquals(SKILL_IS_NOT_FOUND.getMessage(), exception.getMessage());
    }
}