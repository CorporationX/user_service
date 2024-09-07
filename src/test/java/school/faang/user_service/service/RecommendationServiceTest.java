package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.entity.recommendation.dto.RecommendationDto;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.validator.RecommendationDtoValidator;
import school.faang.user_service.validator.SkillInDbValidator;
import school.faang.user_service.validator.UserInDbValidator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {

    @InjectMocks
    private RecommendationService recommendationService;
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private RecommendationDtoValidator recommendationDtoValidator;
    @Mock
    private UserInDbValidator userInDbValidator;
    @Mock
    private SkillInDbValidator skillInDbValidator;
    @Mock
    private RecommendationMapper recommendationMapper;

    private final long USER_ID = 1;

    @Test
    public void testCreateWithSaveRecommendation() {
        RecommendationDto recommendationDto = createRecommendationDto();
        when(userInDbValidator.checkIfUserInDbIsEmpty(recommendationDto.getAuthorId())).thenReturn(getUser());
        when(userInDbValidator.checkIfUserInDbIsEmpty(recommendationDto.getReceiverId())).thenReturn(getUser());
        when(recommendationRepository
                .create(recommendationDto.getAuthorId(), recommendationDto.getReceiverId(), recommendationDto.getContent()))
                .thenReturn(getRecommendation().getId());
        when(recommendationRepository.findById(getRecommendation().getId())).thenReturn(Optional.of(getRecommendation()));

        recommendationService.create(recommendationDto);

        verify(recommendationDtoValidator, times(1))
                .checkIfRecommendationContentIsBlank(recommendationDto);
        verify(recommendationDtoValidator, times(1))
                .checkIfRecommendationCreatedTimeIsShort(recommendationDto);
        verify(userInDbValidator, times(2))
                .checkIfUserInDbIsEmpty(USER_ID);
        verify(skillInDbValidator, times(1)).getSkillsFromDb(recommendationDto);
        verify(recommendationRepository, times(1))
                .create(recommendationDto.getAuthorId(), recommendationDto.getReceiverId(), recommendationDto.getContent());
    }

    @Test
    public void testDelete() {
        recommendationService.delete(USER_ID);
        verify(recommendationRepository, times(1)).deleteById(USER_ID);
    }

    @Test
    public void testGetAllUserRecommendations() {
        List<Recommendation> recommendationList = List.of(new Recommendation(), new Recommendation());
        when(recommendationRepository.findAllByReceiverId(USER_ID, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(recommendationList, Pageable.unpaged(), recommendationList.size()));

        recommendationService.getAllUserRecommendations(USER_ID);
        verify(recommendationRepository, times(1)).findAllByReceiverId(USER_ID, Pageable.unpaged());
    }

    @Test
    public void testGetAllGivenRecommendations() {
        List<Recommendation> recommendationList = List.of(new Recommendation(), new Recommendation());
        when(recommendationRepository.findAllByAuthorId(USER_ID, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(recommendationList, Pageable.unpaged(), recommendationList.size()));

        recommendationService.getAllGivenRecommendations(USER_ID);
        verify(recommendationRepository, times(1)).findAllByAuthorId(USER_ID, Pageable.unpaged());
    }

    private RecommendationDto createRecommendationDto() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setContent("content");
        recommendationDto.setAuthorId(USER_ID);
        recommendationDto.setReceiverId(USER_ID);
        recommendationDto.setCreatedAt(LocalDateTime.now());

        return recommendationDto;
    }

    private Recommendation getRecommendation() {
        Recommendation recommendation = new Recommendation();
        recommendation.setId(10L);
        recommendation.setContent("content");
        recommendation.setAuthor(new User());
        recommendation.setReceiver(new User());
        recommendation.setSkillOffers(List.of(new SkillOffer()));
        recommendation.setRequest(new RecommendationRequest());

        String str = "2014-04-08 12:30";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(str, formatter);

        recommendation.setCreatedAt(dateTime);

        return recommendation;
    }

    private User getUser() {
        User user = new User();
        user.setId(USER_ID);
        return user;
    }
}
