package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.RecommendationValidator;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private SkillRepository skillRepository;
    @Spy
    private RecommendationMapper recommendationMapper = Mappers.getMapper(RecommendationMapper.class);
    @Mock
    private RecommendationValidator recommendationValidator;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @InjectMocks
    RecommendationService recommendationService;

    @Captor
    ArgumentCaptor<Recommendation> captor;

    private static int PAGEABLE_PAGE_PARAMETER = 0;
    private static int PAGEABLE_SIZE_PARAMETER = 10;

    @Test
    public void testCreateWithLastUpdateLessThanSixMonths() {
        RecommendationDto recommendationDto = new RecommendationDto("NotEmpty");
        recommendationDto.setAuthorId(1L);
        recommendationDto.setId(1L);
        recommendationDto.setReceiverId(2L);
        recommendationDto.setUpdatedAt(LocalDateTime.now().minusMonths(4));

        doThrow(DataValidationException.class).when(recommendationValidator)
                .validateBeforeAction(recommendationDto);

        assertThrows(DataValidationException.class, () -> recommendationService.create(recommendationDto));
    }

    @Test
    public void testCreateWithNullSkills() {
        RecommendationDto recommendationDto = new RecommendationDto("NotEmpty");
        recommendationDto.setAuthorId(1L);
        recommendationDto.setId(1L);
        recommendationDto.setReceiverId(2L);
        recommendationDto.setSkillOffers(null);

        doThrow(DataValidationException.class).when(recommendationValidator)
                .validateBeforeAction(recommendationDto);

        assertThrows(DataValidationException.class, () -> recommendationService.create(recommendationDto));
    }

    @Test
    public void testCreateWithNotExistSkillOffers(){
        RecommendationDto recommendationDto = createValidRecommendationDto();

        doThrow(new DataValidationException("The skill with ID : 1 is doesn't exist in the system"))
                .when(recommendationValidator).validateBeforeAction(recommendationDto);

        Exception exception = assertThrows(DataValidationException.class, () ->
                recommendationService.create(recommendationDto));

        assertEquals("The skill with ID : 1 is doesn't exist in the system", exception.getMessage());
    }

    @Test
    public void testCreateSaveRecommendations(){
        RecommendationDto recommendationDto = createValidRecommendationDto();

        doNothing().when(recommendationValidator).validateBeforeAction(recommendationDto);
        recommendationService.create(recommendationDto);

        verify(recommendationRepository, times(1)).save(captor.capture());

        Recommendation recommendation = captor.getValue();

        assertEquals(recommendationDto.getContent(), recommendation.getContent());

    }

    @Test
    public void testUpdateWithValidInput(){
        RecommendationDto recommendationDto = createValidRecommendationDto();

        doNothing().when(recommendationValidator).validateBeforeAction(recommendationDto);
        recommendationService.update(recommendationDto);

        verify(recommendationRepository, times(1))
                .update(recommendationDto.getAuthorId(),
                        recommendationDto.getReceiverId(),
                        recommendationDto.getContent());
    }

    @Test
    public void testDeleteWithInvalidId(){
        long id = 1L;
        doThrow(DataValidationException.class).when(recommendationValidator).validateById(id);
        assertThrows(DataValidationException.class, ()-> recommendationService.delete(id));
    }

    @Test
    public void testDeleteWithValidId(){
        long id = 1L;
        doNothing().when(recommendationValidator).validateById(id);
        recommendationService.delete(id);
        verify(recommendationRepository, times(1)).deleteById(id);
    }

    @Test
    public void testUpdateSuccessfully() {
        RecommendationDto recommendationDto = createValidRecommendationDto();

        Recommendation recommendationEntity = recommendationMapper.toEntity(recommendationDto);

        doNothing().when(recommendationValidator).validateBeforeAction(recommendationDto);
        when(recommendationMapper.toEntity(any(RecommendationDto.class))).thenReturn(recommendationEntity);

        RecommendationDto result = recommendationService.update(recommendationDto);

        verify(recommendationRepository, times(1))
                .update(recommendationEntity.getAuthor().getId(),
                        recommendationEntity.getReceiver().getId(),
                        recommendationEntity.getContent());

        assertEquals(result, recommendationDto);
    }
    /*
    @Test
    public void testGetAllUserRecommendation() {
        long id = 1L;

        Recommendation firstRecommendation = new Recommendation();
        firstRecommendation.setId(1L);
        firstRecommendation.setContent("content");
        firstRecommendation.setSkillOffers(List.of(new SkillOffer(1L,new Skill(),new Recommendation())));

        Recommendation secondRecommendation = new Recommendation();
        secondRecommendation.setId(2L);
        secondRecommendation.setContent("wrong");
        secondRecommendation.setSkillOffers(List.of(new SkillOffer(1L,new Skill(),new Recommendation())));

        List<Recommendation> recommendationsList = List.of(firstRecommendation, secondRecommendation);

        List<RecommendationDto> recommendationDtos = List.of(recommendationMapper.toDto(firstRecommendation),
                recommendationMapper.toDto(secondRecommendation));

        Page<Recommendation> pageRecommendations = new PageImpl<>(recommendationsList);


        when(recommendationRepository.findAllByReceiverId(id,
                PageRequest.of(PAGEABLE_PAGE_PARAMETER, PAGEABLE_SIZE_PARAMETER)))
                .thenReturn(pageRecommendations);
        List<RecommendationDto> result = recommendationService.getAllUserRecommendations(id);

        verify(recommendationRepository, times(1))
                .findAllByReceiverId(id, PageRequest.of(PAGEABLE_PAGE_PARAMETER, PAGEABLE_SIZE_PARAMETER));

        assertEquals(result, recommendationDtos);
    }
    */
    private  RecommendationDto createValidRecommendationDto() {
        RecommendationDto recommendationDto = new RecommendationDto("content");
        recommendationDto.setId(1L);
        recommendationDto.setAuthorId(2L);
        recommendationDto.setReceiverId(3L);
        recommendationDto.setUpdatedAt(LocalDateTime.now().minusMonths(10));
        recommendationDto.setSkillOffers(List.of(
                new SkillOfferDto(1L, 1L),
                new SkillOfferDto(2L, 2L)));
        return recommendationDto;
    }
}

