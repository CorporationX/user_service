package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
import school.faang.user_service.exception.recommendation.DataValidationException;
import school.faang.user_service.exception.recommendation.EntityException;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static school.faang.user_service.exception.recommendation.RecommendationError.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {
    @Mock
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
    private List<SkillOffer> skillOffers;
    private List<Skill> skills;
    private UserSkillGuarantee guarantee;

    @BeforeEach
    void setUp() {
        author = new User();
        author.setId(1L);
        receiver = new User();
        receiver.setId(2L);

        skill = new Skill();
        skill.setId(1L);

        skills = List.of(skill);

        SkillOfferDto skillOfferDto = SkillOfferDto.builder()
                .recommendationId(1L)
                .skillId(1L)
                .build();

        recommendationDto = RecommendationDto.builder()
                .authorId(1L)
                .receiverId(2L)
                .skillOffers(List.of(skillOfferDto))
                .build();

        skillOffer = new SkillOffer();
        skillOffer.setId(1L);
        skillOffer.setSkill(skill);

        recommendation = new Recommendation();
        recommendation.setAuthor(author);
        recommendation.setReceiver(receiver);
        recommendation.setId(1L);
        recommendation.setSkillOffers(List.of(skillOffer));

        skillOffers = List.of(skillOffer);

        guarantee = UserSkillGuarantee.builder()
                .guarantor(author)
                .user(receiver)
                .skill(skill)
                .build();
    }

    @Test
    public void testCreateWithNotExistReceiver() {
        when(userRepository.findById(author.getId())).thenReturn(Optional.of(author));
        when(userRepository.existsById(receiver.getId())).thenReturn(false);

        EntityException ex = assertThrows(EntityException.class,
                () -> recommendationService.create(recommendationDto));

        assertEquals(ex.getMessage(), ENTITY_IS_NOT_FOUND.getMessage());
    }

    @Test
    public void testCreateWithTooShortInterval() {
        recommendation.setCreatedAt(LocalDateTime.now().minusMonths(5));
        author.setRecommendationsGiven(Collections.singletonList(recommendation));

        when(userRepository.findById(author.getId())).thenReturn(Optional.of(author));
        when(userRepository.existsById(receiver.getId())).thenReturn(true);

        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> recommendationService.create(recommendationDto));

        assertEquals(ex.getMessage(), RECOMMENDATION_EXPIRATION_TIME_NOT_PASSED.getMessage());
    }

    @Test
    public void testCreateWithNotFoundSkill() {
        successValidate(Collections.emptyList());

        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> recommendationService.create(recommendationDto));

        assertEquals(ex.getMessage(), SKILL_IS_NOT_FOUND.getMessage());
    }

    @Test
    public void testCreateSuccessWithAddNewGuarantee() {
        successValidate(skillOffers);
        successForCreateGuarantee();

    }

    @Test
    public void testCreateSuccessWithoutAddNewGuaranteeWithAuthorHasGuarantee() {
        successValidate(skillOffers);
        withoutAddGuarantee(skills, true);
    }

    @Test
    public void testUpdateSuccess() {
        successValidate(skillOffers);

        when(recommendationMapper.toEntity(recommendationDto)).thenReturn(recommendation);
        when(skillRepository.findAllByUserId(receiver.getId())).thenReturn(skills);
        when(userSkillGuaranteeRepository.existsById(author.getId())).thenReturn(false);
        when(skillRepository.findById(skill.getId())).thenReturn(Optional.of(skill));
        when(userSkillGuaranteeRepository.save(guarantee)).thenReturn(guarantee);
        when(recommendationMapper.toDto(recommendation)).thenReturn(recommendationDto);

        RecommendationDto result = recommendationService.update(recommendationDto);

        assertNotNull(result);

        verify(skillOfferRepository).deleteAllByRecommendationId(1L);
        verify(recommendationRepository).save(recommendation);
    }

    @Test
    public void deleteRecommendationWhichNotExist() {
        recommendationDto.setId(3L);

        when(recommendationRepository.findById(recommendationDto.getId())).thenReturn(Optional.empty());
        DataValidationException ex = assertThrows(DataValidationException.class, () -> recommendationService.delete(recommendationDto.getId()));

        assertEquals(ex.getMessage(), RECOMMENDATION_IS_NOT_FOUND.getMessage());
    }

    @Test
    public void deleteRecommendationSuccess() {
        recommendationDto.setId(3L);
        recommendation.setId(3L);

        when(recommendationRepository.findById(recommendationDto.getId())).thenReturn(Optional.of(recommendation));
        recommendationService.delete(recommendationDto.getId());

        verify(recommendationRepository).deleteById(recommendationDto.getId());
    }

    @Test
    public void testGetAllUserRecommendations() {
        List<Recommendation> recommendations = List.of(recommendation);
        Page<Recommendation> page = new PageImpl<>(recommendations);

        when(recommendationRepository.findAllByReceiverId(anyLong(), any(Pageable.class))).thenReturn(page);
        when(recommendationMapper.toDto(any(Recommendation.class))).thenReturn(recommendationDto);

        List<RecommendationDto> result = recommendationService.getAllUserRecommendations(receiver.getId(), 0, 10);

        assertEquals(1, result.size());
        verify(recommendationRepository, times(1)).findAllByReceiverId(anyLong(), any(Pageable.class));
    }

    @Test
    void testGetAllGivenRecommendations() {
        List<Recommendation> recommendations = List.of(recommendation);
        Page<Recommendation> page = new PageImpl<>(recommendations);
        when(recommendationRepository.findAllByAuthorId(anyLong(), any(Pageable.class))).thenReturn(page);
        when(recommendationMapper.toDto(any(Recommendation.class))).thenReturn(recommendationDto);

        List<RecommendationDto> result = recommendationService.getAllGivenRecommendations(author.getId(), 0, 10);

        assertEquals(1, result.size());
        verify(recommendationRepository, times(1)).findAllByAuthorId(anyLong(), any(Pageable.class));
    }

    private void successForCreateGuarantee() {
        when(recommendationMapper.toEntity(recommendationDto)).thenReturn(recommendation);
        when(skillRepository.findAllByUserId(receiver.getId())).thenReturn(skills);
        when(userSkillGuaranteeRepository.existsById(author.getId())).thenReturn(false);
        when(skillRepository.findById(skill.getId())).thenReturn(Optional.of(skill));
        when(userSkillGuaranteeRepository.save(guarantee)).thenReturn(guarantee);
        when(recommendationMapper.toDto(recommendation)).thenReturn(recommendationDto);

        recommendationService.create(recommendationDto);

        verify(recommendationMapper).toEntity(recommendationDto);
        verify(skillRepository).findAllByUserId(receiver.getId());
        verify(userSkillGuaranteeRepository).existsById(author.getId());
        verify(skillRepository).findById(skill.getId());
        verify(userSkillGuaranteeRepository).save(guarantee);
        verify(recommendationMapper).toDto(recommendation);
    }

    private void withoutAddGuarantee(List<Skill> receiverSkills, boolean hasGuarantee) {
        when(recommendationMapper.toEntity(recommendationDto)).thenReturn(recommendation);
        when(skillRepository.findAllByUserId(receiver.getId())).thenReturn(receiverSkills);
        when(userSkillGuaranteeRepository.existsById(author.getId())).thenReturn(hasGuarantee);
        when(recommendationMapper.toDto(recommendation)).thenReturn(recommendationDto);

        recommendationService.create(recommendationDto);

        verify(recommendationMapper).toEntity(recommendationDto);
        verify(skillRepository).findAllByUserId(receiver.getId());
        verify(userSkillGuaranteeRepository).existsById(author.getId());
        verify(recommendationMapper).toDto(recommendation);
    }

    private <T> void successValidate(List<T> list) {
        List<Long> skillIds = recommendationDto.getSkillOffers().stream().map(SkillOfferDto::getSkillId).toList();
        recommendation.setCreatedAt(LocalDateTime.now().minusMonths(7));
        author.setRecommendationsGiven(Collections.singletonList(recommendation));

        when(skillOfferRepository.findAllById(skillIds)).thenReturn((Iterable<SkillOffer>) list);
        when(userRepository.findById(author.getId())).thenReturn(Optional.of(author));
        when(userRepository.existsById(receiver.getId())).thenReturn(true);
    }
}