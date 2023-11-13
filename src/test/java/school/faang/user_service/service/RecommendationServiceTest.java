package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
import school.faang.user_service.mapper.RecommendationMapperImpl;
import school.faang.user_service.mapper.SkillOfferMapper;
import school.faang.user_service.mapper.SkillOfferMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Spy
    private SkillOfferMapper skillOfferMapper = new SkillOfferMapperImpl();
    @Spy
    private RecommendationMapper recommendationMapper = new RecommendationMapperImpl(skillOfferMapper);
    @InjectMocks
    private RecommendationService recommendationService;

    private RecommendationDto recommendationDto;
    private Recommendation recommendation;
    private Page<Recommendation> recommendationPage;
    private List<Skill> skills;
    private List<SkillOfferDto> skillOffersDto;
    private List<SkillOffer> skillsOffers;
    private List<UserSkillGuarantee> userSkillGuarantees;
    private UserSkillGuarantee firstGuarantee;
    private UserSkillGuarantee secondGuarantee;
    private SkillOffer skillOffer;
    private SkillOfferDto skillOfferDto;
    private Skill userSkill;
    private User firstUser;
    private User secondUser;
    private User emptyUser;

    private Pageable pageable;

    private final Long authorId = 2L;
    private final Long receiverId = 3L;

    @BeforeEach
    void setUp() {
        this.firstUser = User
                .builder()
                .id(3)
                .build();
        this.secondUser = User
                .builder()
                .id(4)
                .build();
        this.emptyUser = new User();
        this.firstGuarantee = UserSkillGuarantee
                .builder()
                .guarantor(firstUser)
                .build();
        this.secondGuarantee = UserSkillGuarantee
                .builder()
                .guarantor(secondUser)
                .build();
        this.userSkillGuarantees = new ArrayList<>();
        userSkillGuarantees.add(firstGuarantee);
        userSkillGuarantees.add(secondGuarantee);
        this.userSkill = Skill
                .builder()
                .id(1)
                .title("Java")
                .guarantees(userSkillGuarantees)
                .build();
        this.skills = new ArrayList<>(List.of(userSkill));
        this.skillOfferDto = new SkillOfferDto(1L, 1L, 1L);
        this.skillOffersDto = new ArrayList<>(List.of(skillOfferDto));
        this.skillOffer = SkillOffer
                .builder()
                .id(1)
                .skill(userSkill)
                .recommendation(Recommendation.builder().id(1).build())
                .build();
        this.skillsOffers = new ArrayList<>(List.of(skillOffer));
        this.recommendationDto = new RecommendationDto(1L, authorId, receiverId, "Hello", skillOffersDto, LocalDateTime.now());
        this.recommendation = new Recommendation(1, "Hello", firstUser, secondUser, skillsOffers, null, LocalDateTime.now().minusYears(1), null);
        this.recommendationPage = new PageImpl<>(Arrays.asList(recommendation, recommendation), PageRequest.of(0, 5), 2);
        this.pageable = PageRequest.of(0, 5);
    }

    @Test
    void createThrowExceptionWhenRecommendationLessThenSixMonth() {
        Mockito.when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendationDto.getAuthorId(), recommendationDto.getReceiverId()))
                .thenReturn(Optional.of(Recommendation
                        .builder()
                        .createdAt(LocalDateTime.now().minusDays(10)).build()));

        assertThrows(DataValidationException.class, () -> recommendationService.create(recommendationDto));

        Mockito.verify(recommendationRepository)
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendationDto.getAuthorId(), recommendationDto.getReceiverId());
    }

    @Test
    void createThrowExceptionWhenOneOfSkillsNotExistInDatabase() {
        SkillOfferDto nullOffer = null;
        List<SkillOfferDto> skillOffers = new ArrayList<>();

        skillOffers.add(skillOfferDto);
        skillOffers.add(nullOffer);

        RecommendationDto recommendationDto = new RecommendationDto(1L, 2L, 3L, "Hello", skillOffers, LocalDateTime.now());

        Mockito.when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendationDto.getAuthorId(), recommendationDto.getReceiverId()))
                .thenReturn(Optional.of(Recommendation
                        .builder()
                        .createdAt(LocalDateTime.now().minusYears(1)).build()));

        assertThrows(DataValidationException.class, () -> recommendationService.create(recommendationDto));
    }

    @Test
    void createInvokesCreateMethod() {
        Mockito.when(skillRepository.findAllByUserId(receiverId)).thenReturn(skills);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(emptyUser));

        recommendationService.create(recommendationDto);

        Mockito.verify(recommendationRepository).create(authorId, receiverId, recommendationDto.getContent());
    }

    @Test
    void saveSkillOfferThrowExceptiondWhenThereAreSameSkills() {
        List<SkillOfferDto> wrongSkillOffers = List.of(skillOfferDto, new SkillOfferDto(2L, -2L, 2L));
        RecommendationDto firstRecommendationDto = new RecommendationDto(1L, 2L, -3L, "Hello", wrongSkillOffers, LocalDateTime.now());
        RecommendationDto secondRecommendationDto = new RecommendationDto(1L, -2L, 3L, "Hello", wrongSkillOffers, LocalDateTime.now());

        Mockito.when(skillRepository.findAllByUserId(Mockito.anyLong()))
                .thenReturn(List.of(Skill
                        .builder()
                        .title("Hello")
                        .guarantees(userSkillGuarantees)
                        .build()));

        assertThrows(EntityNotFoundException.class, () -> recommendationService.checkAndAddSkillsGuarantorAndUpdateUserSkills(firstRecommendationDto));
        assertThrows(EntityNotFoundException.class, () -> recommendationService.checkAndAddSkillsGuarantorAndUpdateUserSkills(secondRecommendationDto));
    }

    @Test
    void checkAndAddSkillGuarantorAndUpdateUserSkillsThrowExceptionWhenThereAreNoSameSkills() {
        List<SkillOfferDto> wrongSkillOffers = List.of(skillOfferDto, new SkillOfferDto(2L, -2L, 2L));
        RecommendationDto firstRecommendationDto = new RecommendationDto(1L, 2L, -3L, "Hello", wrongSkillOffers, LocalDateTime.now());

        Mockito.when(skillRepository.findAllByUserId(Mockito.anyLong()))
                .thenReturn(List.of(Skill
                        .builder()
                        .title("Hello")
                        .guarantees(userSkillGuarantees)
                        .build()));

        assertThrows(EntityNotFoundException.class, () -> recommendationService.checkAndAddSkillsGuarantorAndUpdateUserSkills(firstRecommendationDto));
    }

    @Test
    void checkAndAddSkillsGuarantorInvokesSaveMethod() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(firstUser));

        recommendationService.checkAndAddSkillsGuarantorAndUpdateUserSkills(recommendationDto);

        Mockito.verify(userRepository).save(firstUser);
    }

    @Test
    void checkAndAddSkillsGuarantorAndUpdateUserSkillsTest() {
        Mockito.when(skillRepository.findAllByUserId(Mockito.anyLong())).thenReturn(skills);
        Mockito.when(skillRepository.findAllById(Mockito.anyList())).thenReturn(List.of(userSkill));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(emptyUser));

        recommendationService.checkAndAddSkillsGuarantorAndUpdateUserSkills(recommendationDto);

        assertEquals(4, skills.get(0).getGuarantees().size());
    }

    @Test
    void testCheckAndAddSkillsGuarantorAndUpdateUserSkillsInvokesFindAllByUserIdMethod() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(emptyUser));

        recommendationService.checkAndAddSkillsGuarantorAndUpdateUserSkills(recommendationDto);

        Mockito.verify(skillRepository).findAllByUserId(3L);
    }

    @Test
    void toDtoShouldReturnCorrectObject() {
        RecommendationDto result = recommendationMapper.toDto(recommendation);
        RecommendationDto expected = new RecommendationDto(1L, firstUser.getId(), secondUser.getId(), "Hello", skillOffersDto, recommendation.getCreatedAt());

        assertEquals(expected, result);
    }

    @Test
    void deleteInvokesDeleteByIdTest() {
        recommendationService.delete(1);

        Mockito.verify(recommendationRepository).deleteById(1L);
    }

    @Test
    void getAllUserRecommendationsTest() {
        RecommendationDto dto = new RecommendationDto(1L, 3L, 4L, "Hello", skillOffersDto, recommendation.getCreatedAt());

        Page<Recommendation> returnRecommendationPage2 = new PageImpl<>(List.of(recommendation));

        Mockito.when(recommendationRepository.findAllByReceiverId(1, pageable))
                .thenReturn(returnRecommendationPage2);

        Page<RecommendationDto> result = recommendationService.getAllUserRecommendations(1, pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(List.of(dto), result.getContent());

    }

    @Test
    void getAllUserRecommendationsInvokesFindAllByReceiverId() {
        Mockito.when(recommendationRepository.findAllByReceiverId(1, pageable))
                .thenReturn(recommendationPage);

        recommendationService.getAllUserRecommendations(1, pageable);

        Mockito.verify(recommendationRepository).findAllByReceiverId(1, pageable);
    }

    @Test
    void getAllUserRecommendationsInvokesToRecommendationDtos() {
        Mockito.when(recommendationRepository.findAllByReceiverId(1, pageable))
                .thenReturn(recommendationPage);

        recommendationService.getAllUserRecommendations(1, pageable);

        Mockito.verify(recommendationMapper).toRecommendationDtos(recommendationPage.getContent());
    }

    @Test
    void getAllUserRecommendationsReturnEmptyList() {
        Mockito.when(recommendationRepository.findAllByReceiverId(1, pageable))
                .thenReturn(Page.empty());

        Page<RecommendationDto> userRecommendations = recommendationService.getAllUserRecommendations(1, pageable);

        assertEquals(Collections.emptyList(), userRecommendations.getContent());
    }

    @Test
    void getAllGivenRecommendationsTest() {
        RecommendationDto dto = new RecommendationDto(1L, 3L, 4L, "Hello", skillOffersDto, recommendation.getCreatedAt());

        Mockito.when(recommendationRepository.findAllByAuthorId(1, pageable))
                .thenReturn(recommendationPage);

        Page<RecommendationDto> result = recommendationService.getAllGivenRecommendations(1, pageable);

        assertEquals(2, result.getContent().size());
        assertEquals(List.of(dto, dto), result.getContent());
    }

    @Test
    void getAllGivenRecommendationsInvokesToRecommendationDtos() {
        Mockito.when(recommendationRepository.findAllByAuthorId(1, pageable))
                .thenReturn(recommendationPage);

        recommendationService.getAllGivenRecommendations(1, pageable);

        Mockito.verify(recommendationMapper).toRecommendationDtos(recommendationPage.getContent());
    }

    @Test
    void getAllGivenRecommendationsInvokesFindAllByReceiverId() {
        Mockito.when(recommendationRepository.findAllByAuthorId(1, pageable))
                .thenReturn(recommendationPage);

        recommendationService.getAllGivenRecommendations(1, pageable);

        Mockito.verify(recommendationRepository).findAllByAuthorId(1, pageable);
    }

    @Test
    void getAllGivenRecommendationsReturnEmptyList() {
        Mockito.when(recommendationRepository.findAllByAuthorId(1, pageable))
                .thenReturn(Page.empty());

        Page<RecommendationDto> givenRecommendations = recommendationService.getAllGivenRecommendations(1, pageable);

        assertEquals(Collections.emptyList(), givenRecommendations.getContent());
    }
}