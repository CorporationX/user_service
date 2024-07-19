package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.recommendation.RecommendationService;
import school.faang.user_service.utils.RecommendationValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {

    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Spy
    private RecommendationMapper recommendationMapper = Mappers.getMapper(RecommendationMapper.class);
    @Mock
    private RecommendationValidator recommendationValidator;

    @InjectMocks
    private RecommendationService recommendationService;

    private final Long createdRecommendationId = 1L;
    private final Long skillOfferId = 1L;
    private final Long skillId = 1L;
    private final Long updatedSkillId = 2L;
    private final LocalDateTime createdAt = LocalDateTime.now();

    private RecommendationDto createUpdateRecommendationDto() {
        return new RecommendationDto(
                createdRecommendationId,
                1L,
                2L,
                "My Content",
                List.of(new SkillOfferDto(null, updatedSkillId, null)),
                createdAt
        );
    };

    private RecommendationDto createExpectedUpdateRecommendationDto() {
        return new RecommendationDto(
                createdRecommendationId,
                1L,
                2L,
                "My Content",
                List.of(new SkillOfferDto(skillOfferId, updatedSkillId, createdRecommendationId)),
                createdAt
        );
    };

    private RecommendationDto createRecommendationDto() {
        return new RecommendationDto(
                null,
                1L,
                2L,
                "My Content",
                List.of(new SkillOfferDto(null, skillId, null)),
                createdAt
        );
    };

    private RecommendationDto createExpectedRecommendationDto() {
        return new RecommendationDto(
                createdRecommendationId,
                1L,
                2L,
                "My Content",
                List.of(new SkillOfferDto(skillOfferId, skillId, createdRecommendationId)),
                createdAt
        );
    };

    private Recommendation createRecommendationFromDto(RecommendationDto recommendationDto) {
        Recommendation recommendation = new Recommendation();

        recommendation.setId(createdRecommendationId);
        User receiver = new User();
        receiver.setId(recommendationDto.receiverId());

        User author = new User();
        author.setId(recommendationDto.authorId());
        author.setRecommendationsGiven(new ArrayList<>());

        recommendation.setAuthor(author);
        recommendation.setReceiver(receiver);
        recommendation.setContent(recommendationDto.content());
        recommendation.setCreatedAt(recommendationDto.createdAt());
        recommendation.setSkillOffers(new ArrayList<>());

        return recommendation;
    };

    private Skill createSkill(Long skillId) {
        Skill skill = new Skill();

        skill.setId(skillId);
        skill.setTitle("JavaScript");

        return skill;
    }

    private SkillOffer createSkillOffer(Long skillId) {
        SkillOffer skillOffer = new SkillOffer();
        skillOffer.setId(skillOfferId);

        skillOffer.setSkill(createSkill(skillId));

        Recommendation recommendation = new Recommendation();
        recommendation.setId(createdRecommendationId);
        skillOffer.setRecommendation(recommendation);

        return skillOffer;
    };

    private UserSkillGuarantee createUserSkillGuarantee(Long skillId) {
        User user = new User();
        user.setId(2L);

        User guarantor = new User();
        guarantor.setId(1L);

        return new UserSkillGuarantee(1L, user, createSkill(skillId), guarantor);
    }

    private Page<Recommendation> createRecommendationPage() {
        Recommendation firstRecommendation = new Recommendation();
        firstRecommendation.setId(1L);

        Recommendation secondRecommendation = new Recommendation();
        secondRecommendation.setId(2L);

        return new PageImpl<>(List.of(firstRecommendation, secondRecommendation));
    }

    private List<RecommendationDto> createRecommendationPageDto() {
        return List.of(
                new RecommendationDto(
                        1L,
                        null,
                        null,
                        null,
                        null,
                        null
                ),
                new RecommendationDto(
                        2L,
                        null,
                        null,
                        null,
                        null,
                        null
                )
        );
    }

    @Test
    public void testCreate() {
        RecommendationDto inputRecommendationDto = createRecommendationDto();
        RecommendationDto expectedRecommendationDto = createExpectedRecommendationDto();

        Mockito.when(recommendationRepository.create(
                inputRecommendationDto.authorId(),
                inputRecommendationDto.receiverId(),
                inputRecommendationDto.content()
        )).thenReturn(createdRecommendationId);
        Mockito.when(recommendationRepository.findById(createdRecommendationId)).thenReturn(Optional.of(createRecommendationFromDto(inputRecommendationDto)));
        Mockito.when(skillRepository.findAllByUserId(Mockito.anyLong())).thenReturn(new ArrayList<>());
        Mockito.when(userSkillGuaranteeRepository.findAllByUserId(Mockito.anyLong())).thenReturn(new ArrayList<>());
        Mockito.when(skillOfferRepository.create(Mockito.anyLong(), Mockito.anyLong())).thenReturn(skillOfferId);
        Mockito.when(skillOfferRepository.findAllById(List.of(skillOfferId))).thenReturn(List.of(createSkillOffer(skillId)));

        RecommendationDto actualRecommendation = recommendationService.create(inputRecommendationDto);

        Assertions.assertEquals(expectedRecommendationDto.id(), actualRecommendation.id(), "id");
        Assertions.assertEquals(expectedRecommendationDto.authorId(), actualRecommendation.authorId(), "authorId");
        Assertions.assertEquals(expectedRecommendationDto.receiverId(), actualRecommendation.receiverId(), "receiverId");
        Assertions.assertEquals(expectedRecommendationDto.content(), actualRecommendation.content(), "content");
        Assertions.assertEquals(expectedRecommendationDto.skillOffers(), actualRecommendation.skillOffers(), "skillOffers");
        Assertions.assertEquals(expectedRecommendationDto.createdAt(), actualRecommendation.createdAt(), "createdAt");

        Mockito.verify(skillOfferRepository, Mockito.times(1)).create(skillId, expectedRecommendationDto.id());
    }

    @Test
    public void testCreateAddToSkillGuarantees() {
        RecommendationDto inputRecommendationDto = createRecommendationDto();

        Mockito.when(recommendationRepository.create(
                inputRecommendationDto.authorId(),
                inputRecommendationDto.receiverId(),
                inputRecommendationDto.content()
        )).thenReturn(createdRecommendationId);
        Mockito.when(recommendationRepository.findById(createdRecommendationId)).thenReturn(Optional.of(createRecommendationFromDto(inputRecommendationDto)));
        Mockito.when(skillRepository.findAllByUserId(inputRecommendationDto.receiverId())).thenReturn(List.of(createSkill(skillId)));
        Mockito.when(skillOfferRepository.create(Mockito.anyLong(), Mockito.anyLong())).thenReturn(skillOfferId);
        Mockito.when(skillOfferRepository.findAllById(List.of(skillOfferId))).thenReturn(List.of(createSkillOffer(skillId)));

        // Еще нет записи в userSkillGuaranteeRepository
        Mockito.when(userSkillGuaranteeRepository.findAllByUserId(inputRecommendationDto.receiverId())).thenReturn(new ArrayList<>());

        recommendationService.create(inputRecommendationDto);

        Mockito.verify(userSkillGuaranteeRepository, Mockito.times(1))
                .create(inputRecommendationDto.receiverId(), skillId, inputRecommendationDto.authorId());
    }

    @Test
    public void testCreateDontAddSkillGuarantees() {
        RecommendationDto inputRecommendationDto = createRecommendationDto();

        Mockito.when(recommendationRepository.create(
                inputRecommendationDto.authorId(),
                inputRecommendationDto.receiverId(),
                inputRecommendationDto.content()
        )).thenReturn(createdRecommendationId);
        Mockito.when(recommendationRepository.findById(createdRecommendationId)).thenReturn(Optional.of(createRecommendationFromDto(inputRecommendationDto)));
        Mockito.when(skillRepository.findAllByUserId(inputRecommendationDto.receiverId())).thenReturn(List.of(createSkill(skillId)));
        Mockito.when(skillOfferRepository.create(Mockito.anyLong(), Mockito.anyLong())).thenReturn(skillOfferId);
        Mockito.when(skillOfferRepository.findAllById(List.of(skillOfferId))).thenReturn(List.of(createSkillOffer(skillId)));

        // Уже есть запись в userSkillGuaranteeRepository
        Mockito.when(userSkillGuaranteeRepository.findAllByUserId(inputRecommendationDto.receiverId())).thenReturn(List.of(createUserSkillGuarantee(skillId)));

        recommendationService.create(inputRecommendationDto);

        Mockito.verify(userSkillGuaranteeRepository, Mockito.times(0))
                .create(inputRecommendationDto.receiverId(), skillId, inputRecommendationDto.authorId());
    }

    @Test
    public void testUpdate() {
        RecommendationDto inputRecommendationDto = createUpdateRecommendationDto();
        RecommendationDto expectedRecommendationDto = createExpectedUpdateRecommendationDto();

        Mockito.when(recommendationRepository.findById(createdRecommendationId)).thenReturn(Optional.of(createRecommendationFromDto(inputRecommendationDto)));
        Mockito.when(skillRepository.findAllByUserId(Mockito.anyLong())).thenReturn(new ArrayList<>());
        Mockito.when(userSkillGuaranteeRepository.findAllByUserId(Mockito.anyLong())).thenReturn(new ArrayList<>());
        Mockito.when(skillOfferRepository.create(Mockito.anyLong(), Mockito.anyLong())).thenReturn(skillOfferId);
        Mockito.when(skillOfferRepository.findAllById(List.of(skillOfferId))).thenReturn(List.of(createSkillOffer(updatedSkillId)));

        RecommendationDto actualRecommendation = recommendationService.update(inputRecommendationDto);

        Assertions.assertEquals(expectedRecommendationDto.id(), actualRecommendation.id(), "id");
        Assertions.assertEquals(expectedRecommendationDto.authorId(), actualRecommendation.authorId(), "authorId");
        Assertions.assertEquals(expectedRecommendationDto.receiverId(), actualRecommendation.receiverId(), "receiverId");
        Assertions.assertEquals(expectedRecommendationDto.content(), actualRecommendation.content(), "content");
        Assertions.assertEquals(expectedRecommendationDto.skillOffers(), actualRecommendation.skillOffers(), "skillOffers");
        Assertions.assertEquals(expectedRecommendationDto.createdAt(), actualRecommendation.createdAt(), "createdAt");

        Mockito.verify(skillOfferRepository, Mockito.times(1)).create(updatedSkillId, expectedRecommendationDto.id());
    }

    @Test
    public void testDelete() {
        recommendationService.delete(createdRecommendationId);

        Mockito.verify(recommendationRepository, Mockito.times(1)).deleteById(createdRecommendationId);
    }

    @Test
    public void testGetAllUserRecommendations() {
        List<RecommendationDto> expectedRecommendations = createRecommendationPageDto();

        Mockito.when(recommendationRepository.findAllByReceiverId(1L, Pageable.unpaged())).thenReturn(createRecommendationPage());

        List<RecommendationDto> actualRecommendations = recommendationService.getAllUserRecommendations(1L);

        Assertions.assertEquals(expectedRecommendations, actualRecommendations);
    }

    @Test
    public void testGetAllGivenRecommendations() {
        List<RecommendationDto> expectedRecommendations = createRecommendationPageDto();

        Mockito.when(recommendationRepository.findAllByAuthorId(1L, Pageable.unpaged())).thenReturn(createRecommendationPage());

        List<RecommendationDto> actualRecommendations = recommendationService.getAllGivenRecommendations(1L);

        Assertions.assertEquals(expectedRecommendations, actualRecommendations);
    }
}
