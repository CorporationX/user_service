package school.faang.user_service.service;

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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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
        this.skills = List.of(userSkill);
        this.skillOfferDto = new SkillOfferDto(1L, 1L, 1L);
        this.skillOffersDto = new ArrayList<>(List.of(skillOfferDto));
        this.skillOffer = SkillOffer
                .builder()
                .id(1)
                .skill(userSkill)
                .recommendation(Recommendation.builder().id(1).build())
                .build();
        this.skillsOffers = new ArrayList<>(List.of(skillOffer));
        this.recommendationDto = new RecommendationDto(1L, 2L, 3L, "Hello", skillOffersDto, LocalDateTime.now());
        this.recommendation = new Recommendation(1, "Hello", firstUser, secondUser, skillsOffers, null, LocalDateTime.now().minusYears(1), null);
        this.recommendationPage = new PageImpl<>(Arrays.asList(recommendation, recommendation), PageRequest.of(0, 2), 2);
    }

    @Test
    void createThrowExceptionWhenAuthorIdIsEmpty() {
        RecommendationDto recommendationDtoWithAuthor = RecommendationDto
                .builder()
                .authorId(null)
                .build();

        assertThrows(DataValidationException.class, () -> recommendationService.create(recommendationDtoWithAuthor));
    }

    @Test
    void createThrowExceptionWhenReceiverIdIsEmpty() {
        RecommendationDto recommendationDtoWithAuthor = RecommendationDto
                .builder()
                .receiverId(null)
                .build();

        assertThrows(DataValidationException.class, () -> recommendationService.create(recommendationDtoWithAuthor));
    }

    @Test
    void createThrowExceptionWhenRecommendationLessThenSixMonth() {
        Mockito.when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendationDto.getAuthorId(), recommendationDto.getReceiverId()))
                .thenReturn(Optional.of(Recommendation
                        .builder()
                        .createdAt(LocalDateTime.now().minusDays(10)).build()));

        recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendationDto.getAuthorId(), recommendationDto.getReceiverId());

        Mockito.verify(recommendationRepository)
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendationDto.getAuthorId(), recommendationDto.getReceiverId());

        assertThrows(DataValidationException.class, () -> recommendationService.create(recommendationDto));
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
        Mockito.when(skillRepository.findAllByUserId(recommendationDto.getReceiverId())).thenReturn(skills);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(emptyUser));

        recommendationService.create(recommendationDto);

        Mockito.verify(recommendationRepository).create(recommendationDto.getAuthorId(), recommendationDto.getReceiverId(), recommendationDto.getContent());
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
        Mockito.when(skillRepository.findAllById(Mockito.anyList()))
                .thenReturn(List.of(Skill
                        .builder()
                        .title("Hello")
                        .guarantees(userSkillGuarantees)
                        .build()));

        assertThrows(DataValidationException.class, () -> recommendationService.saveSkillOffer(firstRecommendationDto));
        assertThrows(DataValidationException.class, () -> recommendationService.saveSkillOffer(secondRecommendationDto));
    }

    @Test
    void saveSkillOfferThrowExceptionWhenThereAreNoSameSkills() {
        List<SkillOfferDto> wrongSkillOffers = List.of(skillOfferDto, new SkillOfferDto(2L, -2L, 2L));
        RecommendationDto firstRecommendationDto = new RecommendationDto(1L, 2L, -3L, "Hello", wrongSkillOffers, LocalDateTime.now());

        Mockito.when(skillRepository.findAllByUserId(Mockito.anyLong()))
                .thenReturn(List.of(Skill
                        .builder()
                        .title("Hello")
                        .guarantees(userSkillGuarantees)
                        .build()));
        Mockito.when(skillRepository.findAllById(Mockito.anyList()))
                .thenReturn(List.of(Skill
                        .builder()
                        .title("Python")
                        .guarantees(userSkillGuarantees)
                        .build()));

        assertThrows(DataValidationException.class, () -> recommendationService.saveSkillOffer(firstRecommendationDto));
    }

    @Test
    void saveSkillOfferInvokesCheckAndAddSkillsGuarantorInvokesSaveMethod() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(firstUser));

        recommendationService.saveSkillOffer(recommendationDto);

        Mockito.verify(userRepository).save(firstUser);
    }

    @Test
    void testSaveSkillOfferInvokesCheckAndAddSkillsGuarantorMethod() {
        Mockito.when(skillRepository.findAllByUserId(Mockito.anyLong())).thenReturn(skills);
        Mockito.when(skillRepository.findAllById(Mockito.anyList())).thenReturn(List.of(userSkill));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(emptyUser));

        recommendationService.saveSkillOffer(recommendationDto);

        assertEquals(3, skills.get(0).getGuarantees().size());
    }

    //
    @Test
    void testSaveSkillOfferInvokesFindAllByUserIdMethod() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(emptyUser));

        recommendationService.saveSkillOffer(recommendationDto);

        Mockito.verify(skillRepository).findAllByUserId(3L);
    }

    @Test
    void saveSkillOfferInvokesFindAllByIdMethod() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(emptyUser));

        recommendationService.saveSkillOffer(recommendationDto);

        Mockito.verify(skillOfferRepository).findAllById(List.of(1L));
    }


    @Test
    void testValidateRecommendationInvokesFindFirstByAuthorIdAndReceiverIdOrderByCreatedAtDescMethod() {
        recommendationService.validateRecommendation(recommendationDto);
        Mockito.verify(recommendationRepository).findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(2, 3);
    }

    @Test
    void testUpdateInvokesUpdateMethod() {
        Mockito.when(userRepository.findById(recommendationDto.getReceiverId()))
                .thenReturn(Optional.of(emptyUser));
        Mockito.when(recommendationMapper.toDto(null))
                .thenReturn(recommendationDto);

        recommendationService.update(recommendationDto);

        Mockito.verify(recommendationRepository).update(recommendationDto.getAuthorId(), recommendationDto.getReceiverId(), recommendationDto.getContent());
    }

    @Test
    void testUpdateInvokesDeleteAllByRecommendationIdMethod() {
        Mockito.when(userRepository.findById(recommendationDto.getReceiverId()))
                .thenReturn(Optional.of(emptyUser));
        Mockito.when(recommendationMapper.toDto(null))
                .thenReturn(recommendationDto);

        recommendationService.update(recommendationDto);

        Mockito.verify(skillOfferRepository).deleteAllByRecommendationId(recommendationDto.getId());
    }

    @Test
    void updateTest() {
        Mockito.when(recommendationRepository.update(recommendationDto.getAuthorId(), recommendationDto.getReceiverId(), recommendationDto.getContent()))
                .thenReturn(recommendation);
        Mockito.when(userRepository.findById(recommendationDto.getReceiverId()))
                .thenReturn(Optional.of(emptyUser));

        RecommendationDto result = recommendationService.update(recommendationDto);
        RecommendationDto expected = new RecommendationDto(1L, firstUser.getId(), secondUser.getId(), "Hello", skillOffersDto, recommendation.getCreatedAt());

        assertEquals(expected, result);
        Mockito.verify(recommendationMapper).toDto(recommendation);
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

        Mockito.when(recommendationRepository.findAllByReceiverId(1, Pageable.unpaged()))
                .thenReturn(recommendationPage);

        List<RecommendationDto> result = recommendationService.getAllUserRecommendations(1);

        assertEquals(2, result.size());
        assertEquals(List.of(dto, dto), result);
    }

    @Test
    void getAllUserRecommendationsInvokesFindAllByReceiverId() {
        Mockito.when(recommendationRepository.findAllByReceiverId(1, Pageable.unpaged()))
                .thenReturn(recommendationPage);

        recommendationService.getAllUserRecommendations(1);

        Mockito.verify(recommendationRepository).findAllByReceiverId(1, Pageable.unpaged());
    }

    @Test
    void getAllUserRecommendationsInvokesToRecommendationDtos() {
        Mockito.when(recommendationRepository.findAllByReceiverId(1, Pageable.unpaged()))
                .thenReturn(recommendationPage);

        recommendationService.getAllUserRecommendations(1);

        Mockito.verify(recommendationMapper).toRecommendationDtos(recommendationPage.getContent());
    }

    @Test
    void getAllUserRecommendationsThrowDataValidationException() {
        Mockito.when(recommendationRepository.findAllByReceiverId(1, Pageable.unpaged()))
                .thenReturn(Page.empty());

        assertThrows(DataValidationException.class, () -> recommendationService.getAllUserRecommendations(1));
    }

    @Test
    void getAllGivenRecommendationsTest() {
        RecommendationDto dto = new RecommendationDto(1L, 3L, 4L, "Hello", skillOffersDto, recommendation.getCreatedAt());

        Mockito.when(recommendationRepository.findAllByAuthorId(1, Pageable.unpaged()))
                .thenReturn(recommendationPage);

        List<RecommendationDto> result = recommendationService.getAllGivenRecommendations(1);

        assertEquals(2, result.size());
        assertEquals(List.of(dto, dto), result);
    }

    @Test
    void getAllGivenRecommendationsInvokesToRecommendationDtos() {
        Mockito.when(recommendationRepository.findAllByAuthorId(1, Pageable.unpaged()))
                .thenReturn(recommendationPage);

        recommendationService.getAllGivenRecommendations(1);

        Mockito.verify(recommendationMapper).toRecommendationDtos(recommendationPage.getContent());
    }

    @Test
    void getAllGivenRecommendationsInvokesFindAllByReceiverId() {
        Mockito.when(recommendationRepository.findAllByAuthorId(1, Pageable.unpaged()))
                .thenReturn(recommendationPage);

        recommendationService.getAllGivenRecommendations(1);

        Mockito.verify(recommendationRepository).findAllByAuthorId(1, Pageable.unpaged());
    }

    @Test
    void getAllGivenRecommendationsThrowDataValidationException() {
        Mockito.when(recommendationRepository.findAllByAuthorId(1, Pageable.unpaged()))
                .thenReturn(Page.empty());

        assertThrows(DataValidationException.class, () -> recommendationService.getAllGivenRecommendations(1));
    }
}