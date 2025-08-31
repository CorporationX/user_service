package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilter;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilterReceiverId;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilterRequesterId;
import school.faang.user_service.mapper.RecommendationRequestMapperImpl;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.validator.recommendation.ValidatorRecommendation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestServiceTest {
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private RecommendationRequestMapperImpl recommendationRequestMapper;
    @Spy
    private UserMapperImpl userMapper;
    @Mock
    private UserContext userContext;

    private List<RecommendationRequestFilter> filters = List.of(new RecommendationRequestFilterRequesterId(),
            new RecommendationRequestFilterReceiverId());
    @Mock
    private ValidatorRecommendation validatorRecommendation;

    @InjectMocks
    private RecommendationRequestServiceImpl recommendationRequestService;

    @Test
    public void testCreated() {
        CreateRecommendationRequestDto dto = new CreateRecommendationRequestDto("Hello", 2L);

        Mockito.when(userRepository.getByIdOrThrow(1L)).thenReturn(new User());
        Mockito.when(userRepository.getByIdOrThrow(2L)).thenReturn(new User());
        Mockito.when(userContext.getUserId()).thenReturn(1L);

        RecommendationRequest entity = recommendationRequestMapper.toRecommendationRequest(dto);
        entity.setRequester(userRepository.getByIdOrThrow(userContext.getUserId()));
        entity.setReceiver(userRepository.getByIdOrThrow(dto.receiverId()));
        entity.setStatus(RequestStatus.PENDING);
        entity.setCreatedAt(LocalDateTime.of(2025, 7, 9, 22, 0));

        RecommendationRequest result = new RecommendationRequest(0, new User(), new User(), "Hello",
                RequestStatus.PENDING, null, null, null,
                LocalDateTime.of(2025, 7, 9, 22, 0), null);
        assertEquals(entity, result);
    }

    @Test
    public void getByFilters() {
        RecommendationRequestFilterDto filtersDto =
                new RecommendationRequestFilterDto(1L, 2L, null, null);
        User[] users = new User[]{new User(), new User(), new User()};
        for (int i = 0; i < users.length; i++) {
            users[i].setId((long) i + 1);
        }
        Mockito.when(recommendationRequestRepository.findAll()).thenReturn(List.of(
                new RecommendationRequest(1L, users[1], users[2], "Hello",
                        RequestStatus.ACCEPTED, null, null, null,
                        LocalDateTime.of(2025, 7, 9, 22, 0),
                        LocalDateTime.of(2025, 7, 9, 23, 0)),
                new RecommendationRequest(2L, users[0], users[1], "Hello",
                        RequestStatus.ACCEPTED, null, null, null,
                        LocalDateTime.of(2025, 7, 9, 22, 0),
                        LocalDateTime.of(2025, 7, 9, 23, 0)),
                new RecommendationRequest(3L, users[0], users[2], "Hello",
                        RequestStatus.ACCEPTED, null, null, null,
                        LocalDateTime.of(2025, 7, 9, 22, 0),
                        LocalDateTime.of(2025, 7, 9, 23, 0))));

        Stream<RecommendationRequest> allRecommendationRequest = recommendationRequestRepository.findAll().stream();
        for (RecommendationRequestFilter filter : filters) {
            if (filter.isApplicable(filtersDto)) {
                allRecommendationRequest = filter.apply(allRecommendationRequest, filtersDto);
            }
        }

        List<RecommendationRequest> result = List.of(
                new RecommendationRequest(2L, users[0], users[1], "Hello",
                        RequestStatus.ACCEPTED, null, null, null,
                        LocalDateTime.of(2025, 7, 9, 22, 0),
                        LocalDateTime.of(2025, 7, 9, 23, 0)));
        assertEquals(allRecommendationRequest.toList(), result);
    }

    @Test
    public void testAccept() {
        Mockito.when(recommendationRequestRepository.getByIdOrThrow(1L))
                .thenReturn(new RecommendationRequest(1L, new User(), new User(), "Hello",
                        RequestStatus.PENDING, null, null, null,
                        LocalDateTime.of(2025, 7, 9, 22, 0), null));

        RecommendationRequest entity = recommendationRequestRepository.getByIdOrThrow(1L);
        entity.setStatus(RequestStatus.ACCEPTED);
        entity.setUpdatedAt(LocalDateTime.of(2025, 7, 9, 23, 0));

        RecommendationRequest result = new RecommendationRequest(1L, new User(), new User(), "Hello",
                RequestStatus.ACCEPTED, null, null, null,
                LocalDateTime.of(2025, 7, 9, 22, 0),
                LocalDateTime.of(2025, 7, 9, 23, 0));
        assertEquals(entity, result);
    }

    @Test
    public void testReject() {
        RejectionDto rejection = new RejectionDto("Not");

        Mockito.when(recommendationRequestRepository.getByIdOrThrow(1L))
                .thenReturn(new RecommendationRequest(1L, new User(), new User(), "Hello",
                        RequestStatus.PENDING, null, null, null,
                        LocalDateTime.of(2025, 7, 9, 22, 0), null));

        RecommendationRequest entity = recommendationRequestRepository.getByIdOrThrow(1L);
        entity.setRejectionReason(rejection.reason());
        entity.setStatus(RequestStatus.REJECTED);
        entity.setUpdatedAt(LocalDateTime.of(2025, 7, 9, 23, 0));

        RecommendationRequest result = new RecommendationRequest(1L, new User(), new User(), "Hello",
                RequestStatus.REJECTED, "Not", null, null,
                LocalDateTime.of(2025, 7, 9, 22, 0),
                LocalDateTime.of(2025, 7, 9, 23, 0));
        assertEquals(entity, result);
    }

}
