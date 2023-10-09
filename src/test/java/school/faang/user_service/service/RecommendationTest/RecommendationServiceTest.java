package school.faang.user_service.service.RecommendationTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationUpdateDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.dto.recommendation.SkillOfferUpdateDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.invalidFieldException.DataValidationException;
import school.faang.user_service.exception.notFoundExceptions.SkillNotFoundException;
import school.faang.user_service.mapper.RecommendationMapperImpl;
import school.faang.user_service.mapper.SkillOfferMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.RecommendationService;
import school.faang.user_service.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @InjectMocks
    private RecommendationService recommendationService;
    @Spy
    private RecommendationMapperImpl recommendationMapper;
    @Spy
    private SkillOfferMapperImpl skillOfferMapper;
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;
    private Skill skill;
    private RecommendationDto recommendationDto;
    private RecommendationUpdateDto recommendationUpdateDto;
    private Recommendation recommendation;
    private Recommendation recommendation1;
    private User receiver;
    private User author;
    @BeforeEach
    void setUp(){
        receiver = new User();
        receiver.setId(2L);

        author = new User();
        author.setId(1L);

        skill = Skill.builder().id(1L).title("Speed").build();

        recommendationDto = new RecommendationDto();
        recommendationDto.setId(3L);
        recommendationDto.setContent("Привет мир");
        recommendationDto.setAuthorId(author.getId());
        recommendationDto.setReceiverId(receiver.getId());
        recommendationDto.setCreatedAt(LocalDateTime.now().minusMonths(7));

        recommendation = recommendationMapper.toEntity(recommendationDto);

        recommendationUpdateDto = new RecommendationUpdateDto();
        recommendationUpdateDto.setId(3L);
        recommendationUpdateDto.setContent("Привет пекарня");
        recommendationUpdateDto.setAuthorId(author.getId());
        recommendationUpdateDto.setReceiverId(receiver.getId());
        recommendationUpdateDto.setUpdatedAt(LocalDateTime.now().minusMonths(7));

        recommendation1 = recommendationMapper.toUpdateEntity(recommendationUpdateDto);
    }

    @Test
    public void testCreate_Successful(){
        recommendationDto.setSkillOffers(new ArrayList<>());
        recommendation = recommendationMapper.toEntity(recommendationDto);

        Mockito.when(recommendationRepository.create(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent()))
                .thenReturn(recommendation.getId());
        Mockito.when(recommendationRepository.findById(recommendationDto.getId()))
                .thenReturn(Optional.of(recommendation));
        recommendationService.create(recommendationDto);
        Mockito.verify(recommendationRepository).save(Mockito.any());
    }

    @Test
    public void testCreate_RecommendationIsNotFound_ShouldThrowException(){
        Mockito.when(recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(1L,2L))
                .thenReturn(Optional.empty());

        assertThrows(SkillNotFoundException.class,
                ()-> recommendationService.create(RecommendationDto
                        .builder().authorId(1L)
                        .content("Hi")
                        .receiverId(2L)
                        .skillOffers(List.of(new SkillOfferDto()))
                        .build()));
    }

    @Test
    public void testCreate_RecommendationLessThenSixMonth_ShouldThrowException(){
        Mockito.when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(1L,2L))
                .thenReturn(Optional.of(Recommendation
                        .builder()
                        .createdAt(LocalDateTime.now().minusMonths(5))
                        .build()));

        assertThrows(DataValidationException.class,
                ()-> recommendationService.create(RecommendationDto
                        .builder().authorId(1L)
                        .content("Hi")
                        .receiverId(2L)
                        .skillOffers(List.of(new SkillOfferDto()))
                        .build()));

        Mockito.verify(recommendationRepository, Mockito.times(1))
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc
                        (1L,2L);
    }

    @Test
    public void testCreate_CheckSkillsInRepository_ShouldThrowException(){
        SkillOfferDto skillOfferDto = SkillOfferDto
                .builder()
                .skillId(1L)
                .recommendationId(2L)
                .id(5L)
                .build();

        SkillOfferDto skillOfferDto1 = SkillOfferDto
                .builder()
                .skillId(2L)
                .recommendationId(2L)
                .id(5L)
                .build();

        List<SkillOfferDto> skillOfferDtos = List.of(skillOfferDto1, skillOfferDto);

        recommendationDto.setSkillOffers(skillOfferDtos);

        assertThrows(SkillNotFoundException.class,
                () -> recommendationService.create(recommendationDto));
    }

    @Test
    public void testUpdate_RecommendationIsNotFound_ShouldThrowException(){
        Mockito.when(recommendationRepository
                        .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(1L,2L))
                .thenReturn(Optional.empty());

        assertThrows(SkillNotFoundException.class,
                ()-> recommendationService.update(RecommendationUpdateDto
                        .builder().authorId(1L)
                        .content("Hi")
                        .receiverId(2L)
                        .skillOffers(List.of(new SkillOfferUpdateDto()))
                        .build()));
    }

    @Test
    public void testUpdate_RecommendationLessThenSixMonth_ShouldThrowException(){
        Mockito.when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(1L,2L))
                .thenReturn(Optional.empty());

        assertThrows(SkillNotFoundException.class,
                ()-> recommendationService.update(RecommendationUpdateDto
                        .builder().authorId(1L)
                        .receiverId(2L)
                        .content("Hi")
                        .skillOffers(List.of(new SkillOfferUpdateDto()))
                        .build()));

        Mockito.verify(recommendationRepository, Mockito.times(1))
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc
                        (1L,2L);
    }

    @Test
    public void testUpdate_SkillNotFoundInDB_ShouldThrowException(){
        SkillOfferUpdateDto skillOfferUpdateDto = SkillOfferUpdateDto
                .builder()
                .skillId(skill.getId())
                .recommendationId(2L)
                .id(5L)
                .build();
        recommendationUpdateDto.setSkillOffers(List.of(skillOfferUpdateDto));

        recommendationUpdateDto.getSkillOffers()
                .forEach(skillOffer -> skillRepository.existsById(skillOffer.getSkillId()));

        assertThrows(SkillNotFoundException.class,
                () -> recommendationService.update(RecommendationUpdateDto
                        .builder().authorId(1L)
                        .skillOffers(List.of(new SkillOfferUpdateDto()))
                        .receiverId(2L).build()));
    }

    @Test
    public void testDelete_deleteRecommendation() {
        long recommendationId = 1L;
        recommendationService.delete(recommendationId);
        Mockito.verify(recommendationRepository, Mockito.times(1)).deleteById(recommendationId);
    }

    @Test
    public void testGetAllUserRecommendations_checkNull(){
        Page<Recommendation> recommendations = recommendationRepository
                .findAllByReceiverId(recommendationDto.getReceiverId(), Pageable.unpaged());
        Mockito.when(recommendationRepository
                .findAllByReceiverId(recommendationDto.getReceiverId(), Pageable.unpaged()))
                .thenReturn(null);
        assertTrue(recommendationService.getAllUserRecommendations(recommendationDto.getReceiverId()).isEmpty());
    }

    @Test
    public void testGetAllGivenRecommendations_checkNull(){
        Page<Recommendation> recommendations = recommendationRepository
                .findAllByReceiverId(recommendationDto.getAuthorId(), Pageable.unpaged());
        Mockito.when(recommendationRepository
                .findAllByReceiverId(recommendationDto.getAuthorId(), Pageable.unpaged()))
                .thenReturn(null);
        assertTrue(recommendationService.getAllUserRecommendations(recommendationDto.getAuthorId()).isEmpty());
    }
}