package school.faang.user_service.service.mentorship;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.model.dto.RequestFilter;
import school.faang.user_service.model.entity.MentorshipRequest;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.mapper.RequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.MentorshipRequestService;
import school.faang.user_service.service.impl.mentorship.MentorshipRequestServiceImpl;
import school.faang.user_service.validator.MentorshipRequestValidator;
import school.faang.user_service.validator.PredicatesImpl;
import school.faang.user_service.validator.RequestFilterPredicate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentroshipReqSerGetRequestTest {
    @Mock
    MentorshipRequestValidator mentorshipRequestValidator;

    @Mock
    private RequestMapper requestMapper;

    @Mock
    MentorshipRequestRepository repository;

    private RequestFilterPredicate predicates;

    MentorshipRequestService service;

    @BeforeEach
    public void setup() {
        predicates = new PredicatesImpl();
        service = new MentorshipRequestServiceImpl(mentorshipRequestValidator, repository, predicates);
    }

    @Test
    public void shouldReturnEmptyList_WhenFilterIsEmpty() {
        // Arrange
        RequestFilter filter = RequestFilter.builder().build();

        List<MentorshipRequest> allRequests = List.of(
                MentorshipRequest.builder().id(1L).description("Description 1")
                        .receiver(User.builder().id(1L).build())
                        .requester(User.builder().id(2L).build())
                        .build(),
                MentorshipRequest.builder().id(1L).description("Description 2")
                        .receiver(User.builder().id(1L).build())
                        .requester(User.builder().id(2L).build()).build()
        );

        when(repository.findAll()).thenReturn(allRequests);

        // Act
        List<MentorshipRequest> result = service.getRequests(filter);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    public void shouldReturnList_WhenSomeFilterMatch() {
        // Arrange
        RequestFilter filter = RequestFilter.builder().requesterId(2L).receiverId(1L).build();
        MentorshipRequest m1 = MentorshipRequest.builder().id(1L)
                .receiver(User.builder().id(1L).build())
                .requester(User.builder().id(2L).build())
                .build();

        MentorshipRequest m2 = MentorshipRequest.builder().id(1L)
                .receiver(User.builder().id(1L).build())
                .requester(User.builder().id(3L).build()).build();
        List<MentorshipRequest> allRequests = List.of(
                m1, m2
        );

        when(repository.findAll()).thenReturn(allRequests);

        // Act
        List<MentorshipRequest> result = service.getRequests(filter);
        System.out.println(result);

        // Assert
        assertThat(result).isEqualTo( List.of(m1));
    }
    @Test
    public void shouldReturnList_WhenAllFilterMatch() {
        // Arrange
        RequestFilter filter = RequestFilter.builder().requesterId(2L).receiverId(1L).build();
        MentorshipRequest m1 = MentorshipRequest.builder().id(1L)
                .receiver(User.builder().id(1L).build())
                .requester(User.builder().id(2L).build())
                .build();

        MentorshipRequest m2 = MentorshipRequest.builder().id(1L)
                .receiver(User.builder().id(1L).build())
                .requester(User.builder().id(2L).build()).build();
        List<MentorshipRequest> allRequests = List.of(
                m1, m2
        );

        when(repository.findAll()).thenReturn(allRequests);

        // Act
        List<MentorshipRequest> result = service.getRequests(filter);
        System.out.println(result);

        // Assert
        assertThat(result).isEqualTo( List.of(m1,m2));
    }


}

