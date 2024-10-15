package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.model.dto.MentorshipRequestDto;
import school.faang.user_service.model.entity.MentorshipRequest;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.validatorResult.NotValidated;
import school.faang.user_service.validator.validatorResult.Validated;
import school.faang.user_service.validator.validatorResult.ValidationResult;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.when;
import static school.faang.user_service.validator.PredicatesImpl.*;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestValidatorTest {
    @Mock
    private MentorshipRequestRepository repository;
    @Spy
    private MentorshipFilterPredicate predicates;
    private MentorshipRequestValidatorImpl validator;

    @BeforeEach
    public void setup() {
        predicates = new PredicatesImpl();
        validator = new MentorshipRequestValidatorImpl( repository, predicates);
    }


    @Test
    void validate_request_accepted() {
        MentorshipRequest request = new MentorshipRequest();
        request.setUpdatedAt(LocalDateTime.now().minus(3, ChronoUnit.MONTHS));
        MentorshipRequestDto dto = MentorshipRequestDto.builder().requesterId(1L).receiverId(2L).build();

        when(repository.existAcceptedRequest(dto.getRequesterId(), dto.getReceiverId())).thenReturn(true);

        ValidationResult status = validator.validate(dto);

        assertInstanceOf(Validated.class, status, "Status should be an instance of Validated");
    }

    @Test
    void validate_request_not_accepted_when_update_was_before_3_month() {
        MentorshipRequest request = new MentorshipRequest();
        request.setUpdatedAt(LocalDateTime.now().minus(1, ChronoUnit.MONTHS));
        MentorshipRequestDto dto = MentorshipRequestDto.builder().requesterId(1L).receiverId(2L)
                .updatedAt(LocalDateTime.now()).createdAt(LocalDateTime.now()).build();

        when(repository.existAcceptedRequest(dto.getRequesterId(), dto.getReceiverId())).thenReturn(true);
        when(repository.findLatestRequest(dto.getRequesterId(), dto.getReceiverId()))
                .thenReturn(Optional.of(request));

        NotValidated status = (NotValidated) validator.validate(dto);

        assertEquals(status.getMessage(), REQUEST_TIME_EXEEDED);

    }


    @Test
    void validate_request_not_accepted_because_user_not_found() {
        MentorshipRequestDto dto = MentorshipRequestDto.builder().requesterId(1L).receiverId(2L).build();

        when(repository.existAcceptedRequest(dto.getRequesterId(), dto.getReceiverId())).thenReturn(false);

        NotValidated status = (NotValidated) validator.validate(dto);
        assertEquals(status.getMessage(), USERS_NOT_EXIST_IN_DATABASE);

    }

    @Test
    void validate_request_not_accepted_because_user_has_the_same_id() {
        MentorshipRequestDto dto = MentorshipRequestDto.builder().requesterId(1L).receiverId(1L).build();

        when(repository.existAcceptedRequest(dto.getRequesterId(), dto.getReceiverId())).thenReturn(true);

        NotValidated status = (NotValidated) validator.validate(dto);
        assertEquals(status.getMessage(), REQUEST_AND_RECEIVER_HAS_SAME_ID);

    }

    @Test
    void validate_request_not_accepted_because_request_was_not_found() {
        MentorshipRequest request = new MentorshipRequest();
        request.setUpdatedAt(LocalDateTime.now().minus(1, ChronoUnit.MONTHS));
        MentorshipRequestDto dto = MentorshipRequestDto.builder().requesterId(1L).receiverId(2L)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

        when(repository.existAcceptedRequest(dto.getRequesterId(), dto.getReceiverId())).thenReturn(true);
        when(repository.findLatestRequest(dto.getRequesterId(), dto.getReceiverId()))
                .thenReturn(Optional.empty());

        NotValidated status = (NotValidated) validator.validate(dto);
        assertEquals(status.getMessage(), REQUEST_WAS_NOT_FOUND);

    }
}