package school.faang.user_service.service;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.controller.recommendation.RecommendationRequestController;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.SkillRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.mapper.RecommendationRequestMapperImpl;
import school.faang.user_service.mapper.SkillRequestMapper;
import school.faang.user_service.mapper.SkillRequestMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.filter.recommendation.RequestFilter;
import school.faang.user_service.service.filter.recommendation.RequestFilterDto;
import school.faang.user_service.service.filter.recommendation.RequestFilterMessage;
import school.faang.user_service.service.filter.recommendation.RequestFilterStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestServiceTest {
    private RecommendationRequestDto dto;
    private RecommendationRequestDto expectedDto;
    private RecommendationRequest request;
    private RecommendationRequest expectedRequest;
    private long id;
    private RejectionDto rejection;
    @Captor
    ArgumentCaptor<Long> captorRequesterId;
    @Captor
    ArgumentCaptor<Long> captorSkillId;
    @Captor
    ArgumentCaptor<RecommendationRequest> captorRequest;
    @InjectMocks
    private RecommendationRequestService service;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RecommendationRequestRepository requestRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillRequestRepository skillRequestRepository;
    @Spy
    private List<RequestFilter> requestFilters;
    @Spy
    private RecommendationRequestMapper mapper = Mappers.getMapper(RecommendationRequestMapper.class);
    @Spy
    private SkillRequestMapper skillRequestMapper = Mappers.getMapper(SkillRequestMapper.class);

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(mapper,"skillRequestMapper", skillRequestMapper);
        requestFilters = new ArrayList<>(
                List.of(
                        new RequestFilterMessage(),
                        new RequestFilterStatus()
                )
        );
        service = new RecommendationRequestService(
                requestRepository,
                userRepository,
                skillRequestRepository,
                skillRepository,
                mapper,
                requestFilters
        );
        dto = new RecommendationRequestDto(
                1L,
                "message",
                RequestStatus.PENDING,
                new ArrayList<>(List.of(
                        SkillRequestDto.builder().id(1L).skillId(1L).requestId(2L).build(),
                        SkillRequestDto.builder().id(2L).skillId(2L).requestId(2L).build()
                )),
                2L,
                3L,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        expectedDto = new RecommendationRequestDto(
                1L,
                "message",
                RequestStatus.PENDING,
                new ArrayList<>(List.of(
                        SkillRequestDto.builder().id(1L).skillId(1L).requestId(2L).build(),
                        SkillRequestDto.builder().id(2L).skillId(2L).requestId(2L).build()
                )),
                2L,
                3L,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        request = RecommendationRequest.builder()
                .id(1L)
                .message("message")
                .status(RequestStatus.PENDING)
                .skills(new ArrayList<>(List.of(
                        new SkillRequest(1L, RecommendationRequest.builder().id(2L).build(), Skill.builder().id(1L).build()),
                        new SkillRequest(2L, RecommendationRequest.builder().id(2L).build(), Skill.builder().id(2L).build())
                )))
                .requester(User.builder().id(2L).build())
                .receiver(User.builder().id(3L).build())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
        expectedRequest = RecommendationRequest.builder()
                .id(1L)
                .message("message")
                .status(RequestStatus.PENDING)
                .skills(new ArrayList<>(List.of(
                        new SkillRequest(1L, RecommendationRequest.builder().id(2L).build(), Skill.builder().id(1L).build()),
                        new SkillRequest(2L, RecommendationRequest.builder().id(2L).build(), Skill.builder().id(2L).build())
                )))
                .requester(User.builder().id(2L).build())
                .receiver(User.builder().id(3L).build())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();

        id = 1L;
        rejection = new RejectionDto("reason");
    }


    @Test
    public void testCreate_requesterThereIsNotDb() {
        // Arrange
        when(userRepository.findById(dto.getRequesterId())).thenReturn(Optional.empty());

        // Act and Assert
        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {service.create(dto);});
        Assertions.assertEquals("Requester отсутствует в базе данных", exception.getMessage());
    }

    @Test
    public void testCreate_receiverThereIsNotDb() {
        // Arrange
        when(userRepository.findById(dto.getRequesterId())).thenReturn(Optional.of(User.builder().id(dto.getRequesterId()).build()));
        when(userRepository.findById(dto.getReceiverId())).thenReturn(Optional.empty());

        // Act and Assert
        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {service.create(dto);});
        Assertions.assertEquals("Receiver отсутствует в базе данных", exception.getMessage());
    }

    @Test
    public void testCreate_frequentRequest() {
        // Arrange
        when(userRepository.findById(dto.getRequesterId())).thenReturn(Optional.of(User.builder().id(dto.getRequesterId()).build()));
        when(userRepository.findById(dto.getReceiverId())).thenReturn(Optional.of(User.builder().id(dto.getReceiverId()).build()));
        when(requestRepository.findLatestPendingRequest(dto.getRequesterId(), dto.getReceiverId()))
                .thenReturn(Optional.of(RecommendationRequest.builder().createdAt(LocalDateTime.now().minusMonths(3)).build()));

        // Act and Assert
        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {service.create(dto);});
        Assertions.assertEquals("Запрос рекомендации можно отправлять не чаще, чем один раз в 6 месяцев", exception.getMessage());
    }

    @Test
    public void testCreate_thereIsNotSkills() {
        // Arrange
        when(userRepository.findById(dto.getRequesterId())).thenReturn(Optional.of(User.builder().id(dto.getRequesterId()).build()));
        when(userRepository.findById(dto.getReceiverId())).thenReturn(Optional.of(User.builder().id(dto.getReceiverId()).build()));
        when(requestRepository.findLatestPendingRequest(dto.getRequesterId(), dto.getReceiverId()))
                .thenReturn(Optional.of(RecommendationRequest.builder().createdAt(LocalDateTime.now().minusMonths(7)).build()));
        dto.setSkills(new ArrayList<>());

        // Act and Assert
        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {service.create(dto);});
        Assertions.assertEquals("Скиллы отсутствуют в запросе", exception.getMessage());
    }

    @Test
    public void testCreate_nullSkills() {
        // Arrange
        when(userRepository.findById(dto.getRequesterId())).thenReturn(Optional.of(User.builder().id(dto.getRequesterId()).build()));
        when(userRepository.findById(dto.getReceiverId())).thenReturn(Optional.of(User.builder().id(dto.getReceiverId()).build()));
        when(requestRepository.findLatestPendingRequest(dto.getRequesterId(), dto.getReceiverId()))
                .thenReturn(Optional.of(RecommendationRequest.builder().createdAt(LocalDateTime.now().minusMonths(7)).build()));
        dto.setSkills(null);

        // Act and Assert
        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {service.create(dto);});
        Assertions.assertEquals("Скиллы отсутствуют в запросе", exception.getMessage());
    }

    @Test
    public void testCreate_thereIsNotSkillsInDb() {
        // Arrange
        when(userRepository.findById(dto.getRequesterId())).thenReturn(Optional.of(User.builder().id(dto.getRequesterId()).build()));
        when(userRepository.findById(dto.getReceiverId())).thenReturn(Optional.of(User.builder().id(dto.getReceiverId()).build()));
        when(requestRepository.findLatestPendingRequest(dto.getRequesterId(), dto.getReceiverId()))
                .thenReturn(Optional.of(RecommendationRequest.builder().createdAt(LocalDateTime.now().minusMonths(7)).build()));
        when(skillRepository.countExisting(any())).thenReturn(dto.getSkills().size() - 1);

        // Act and Assert
        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {service.create(dto);});
        Assertions.assertEquals("Не все скиллы существуют в базе данных", exception.getMessage());
    }

    @Test
    public void testCreate(){
        // Arrange
        when(userRepository.findById(dto.getRequesterId())).thenReturn(Optional.of(User.builder().id(dto.getRequesterId()).build()));
        when(userRepository.findById(dto.getReceiverId())).thenReturn(Optional.of(User.builder().id(dto.getReceiverId()).build()));
        when(requestRepository.findLatestPendingRequest(dto.getRequesterId(), dto.getReceiverId()))
                .thenReturn(Optional.of(RecommendationRequest.builder().createdAt(LocalDateTime.now().minusMonths(7)).build()));
        when(skillRepository.countExisting(any())).thenReturn(dto.getSkills().size());
        when(requestRepository.save(request)).thenReturn(expectedRequest);

        // Act and Assert
        RecommendationRequestDto returnDto = service.create(dto);
        verify(skillRequestRepository, times(dto.getSkills().size())).create(captorRequesterId.capture(), captorSkillId.capture());
        List<Long> requesterIds = captorRequesterId.getAllValues();
        List<Long> skillIds = captorSkillId.getAllValues();
        for (int i = 0; i < dto.getSkills().size(); i++) {
            Assertions.assertEquals(dto.getRequesterId(), requesterIds.get(i));
            Assertions.assertEquals(dto.getSkills().get(i).getId(), skillIds.get(i));
        }

        verify(requestRepository, times(1)).save(captorRequest.capture());
        Assertions.assertEquals(expectedRequest, captorRequest.getValue());
        Assertions.assertEquals(expectedDto, returnDto);
    }

    @Test
    public void testGetRequests() {
        // Arrange
        RequestFilterDto filterDto = new RequestFilterDto(RequestStatus.ACCEPTED, "message");
        List<RecommendationRequest> returnRequests = new ArrayList<>(
                List.of(
                        RecommendationRequest.builder().id(1L).message("mess").status(RequestStatus.ACCEPTED).build(),
                        RecommendationRequest.builder().id(2L).message("message").status(RequestStatus.PENDING).build(),
                        RecommendationRequest.builder().id(3L).message("message").status(RequestStatus.ACCEPTED).build()
                )
        );
        when(requestRepository.findAll()).thenReturn(returnRequests);
        List<RecommendationRequestDto> expectedDtos = new ArrayList<>(
                List.of(
                        RecommendationRequestDto.builder().id(3L).message("message").status(RequestStatus.ACCEPTED).build()
                )
        );
        // Act
        List<RecommendationRequestDto> receivedDtos = service.getRequests(filterDto);

        // Assert
        Assertions.assertEquals(expectedDtos, receivedDtos);
    }

    @Test
    public void testGetRequest() {
        // Arrange
        long id = 1L;
        when(requestRepository.findById(id)).thenReturn(Optional.of(expectedRequest));
        //service.set

        // Act
        RecommendationRequestDto returnDto = service.getRequest(id);

        // Assert
        Assertions.assertEquals(expectedDto, returnDto);
    }

    @Test
    public void testRejectRequest_thereNoExistRequestInDb() {
        // Arrange
        when(requestRepository.findById(id)).thenReturn(Optional.empty());

        // Act and Assert
        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {service.rejectRequest(id, rejection);});
        Assertions.assertEquals("Запрашиваемого запроса нет в базе данных", exception.getMessage());
    }

    @Test
    public void testRejectRequest_requestRejectedAlready() {
        // Arrange
        expectedRequest.setStatus(RequestStatus.REJECTED);
        when(requestRepository.findById(id)).thenReturn(Optional.of(expectedRequest));

        // Act and Assert
        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {service.rejectRequest(id, rejection);});
        Assertions.assertEquals("Запрос уже был отклонён", exception.getMessage());
    }

    @Test
    public void testRejectRequest_requestAcceptedAlready() {
        // Arrange
        expectedRequest.setStatus(RequestStatus.ACCEPTED);
        when(requestRepository.findById(id)).thenReturn(Optional.of(expectedRequest));

        // Act and Assert
        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {service.rejectRequest(id, rejection);});
        Assertions.assertEquals("Запрос уже принят, нельзя отклонить принятый запрос", exception.getMessage());
    }

    @Test
    public void testRejectRequest() {
        // Arrange
        request.setStatus(RequestStatus.PENDING);
        when(requestRepository.findById(id)).thenReturn(Optional.of(request));
        expectedRequest.setRejectionReason("reason");
        expectedRequest.setStatus(RequestStatus.REJECTED);

        // Act and Assert
        service.rejectRequest(id, rejection);
        verify(requestRepository, times(1)).save(captorRequest.capture());
        Assertions.assertEquals(expectedRequest, captorRequest.getValue());
    }
}
