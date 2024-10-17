package school.faang.user_service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.model.dto.RequestFilter;
import school.faang.user_service.model.entity.MentorshipRequest;
import school.faang.user_service.model.entity.RequestStatus;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.util.predicate.NotApplicable;
import school.faang.user_service.util.predicate.PredicateFalse;
import school.faang.user_service.util.predicate.PredicateResult;
import school.faang.user_service.util.predicate.PredicateTrue;
import school.faang.user_service.validator.PredicatesImpl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static school.faang.user_service.validator.PredicatesImpl.*;

@ExtendWith(MockitoExtension.class)
public class RequestFilterPredicatesTest {
    @Spy
    private PredicatesImpl predicates;

    @Test
    void testAreAuthorsMatch_ShouldReturnTrue_WhenRequesterIdMatches() {
        // Arrange
        MentorshipRequest request = MentorshipRequest.builder().id(1L).requester(User.builder().id(1L).build()).build();
        RequestFilter filter =  RequestFilter.builder().requesterId(1L).build(); // Requester ID matches

        // Act
        PredicateResult result = predicates.areAuthorsMatch.apply(request, filter);

        // Assert
        assertTrue(result instanceof PredicateTrue);
    }

    @Test
    void testAreAuthorsMatch_ShouldReturnFalse_WhenRequesterIdDoesNotMatch() {
        // Arrange
        MentorshipRequest request = MentorshipRequest.builder().id(1L).requester(User.builder().id(1L).build()).build();
        RequestFilter filter =  RequestFilter.builder().requesterId(2L).build();

        // Act
        PredicateResult result = predicates.areAuthorsMatch.apply(request, filter);

        // Assert
        assertTrue(result instanceof PredicateFalse);
        assertEquals(REQUESTER_ID_DONT_MATCH, ((PredicateFalse) result).message());
    }

    @Test
    void testAreAuthorsMatch_ShouldReturnNotApplicable_WhenRequesterIdIsNull() {
        // Arrange
        MentorshipRequest request = MentorshipRequest.builder().id(1L).requester(User.builder().id(1L).build()).build();
        RequestFilter filter =  RequestFilter.builder().build();

        // Act
        PredicateResult result = predicates.areAuthorsMatch.apply(request, filter);

        // Assert
        assertTrue(result instanceof NotApplicable);
    }

    @Test
    void testIsReceiverMatch_ShouldReturnTrue_WhenReceiverIdMatches() {
        // Arrange
        MentorshipRequest request = MentorshipRequest.builder().id(1L).receiver(User.builder().id(1L).build()).build();
        RequestFilter filter =  RequestFilter.builder().receiverId(1L).build();

        // Act
        PredicateResult result = predicates.isRecieverMatch.apply(request, filter);

        // Assert
        assertTrue(result instanceof PredicateTrue);
    }

    @Test
    void testIsReceiverMatch_ShouldReturnFalse_WhenReceiverIdDoesNotMatch() {
        // Arrange
        MentorshipRequest request = MentorshipRequest.builder().id(1L).receiver(User.builder().id(1L).build()).build();
        RequestFilter filter =  RequestFilter.builder().receiverId(3L).build();

        // Act
        PredicateResult result = predicates.isRecieverMatch.apply(request, filter);

        // Assert
        assertThat(result).isInstanceOf(PredicateFalse.class);
        assertEquals(RECEIVER_ID_DONT_MATCH, ((PredicateFalse) result).message());
    }

    @Test
    void testIsReceiverMatch_ShouldReturnNotApplicable_WhenReceiverIdIsNull() {
        // Arrange
        MentorshipRequest request = MentorshipRequest.builder().id(1L).receiver(User.builder().build()).build();
        RequestFilter filter =  RequestFilter.builder().requesterId(3L).build();

        // Act
        PredicateResult result = predicates.isRecieverMatch.apply(request, filter);

        // Assert
        assertTrue(result instanceof NotApplicable);
    }

    @Test
    void testIsStatusMatch_ShouldReturnTrue_WhenStatusMatches() {
        // Arrange
        MentorshipRequest request = MentorshipRequest.builder().status(RequestStatus.REJECTED).build();
        RequestFilter filter =  RequestFilter.builder().status(RequestStatus.REJECTED).build();

        // Act
        PredicateResult result = predicates.isStatusMatch.apply(request, filter);

        // Assert
        assertTrue(result instanceof PredicateTrue);
    }

    @Test
    void testIsStatusMatch_ShouldReturnFalse_WhenStatusDoesNotMatch() {
        // Arrange
        MentorshipRequest request = MentorshipRequest.builder().status(RequestStatus.PENDING).build();
        RequestFilter filter =  RequestFilter.builder().status(RequestStatus.REJECTED).build();

        // Act
        PredicateResult result = predicates.isStatusMatch.apply(request, filter);

        // Assert
        assertTrue(result instanceof PredicateFalse);
        assertEquals(STATUS_DONT_MATCH, ((PredicateFalse) result).message());
    }

    @Test
    void testIsStatusMatch_ShouldReturnNotApplicable_WhenStatusIsNull() {
        // Arrange
        MentorshipRequest request = MentorshipRequest.builder().status(RequestStatus.PENDING).build();
        RequestFilter filter =  RequestFilter.builder().build();

        // Act
        PredicateResult result = predicates.isStatusMatch.apply(request, filter);

        // Assert
        assertTrue(result instanceof NotApplicable);
    }


    @Test
    void testIsDescriptionEmpty_ShouldReturnNotApplicable_whenEmpty() {
        MentorshipRequest request = MentorshipRequest.builder().build();
        RequestFilter filter =  RequestFilter.builder().build();

        // Act
        PredicateResult result = predicates.isDescriptionEmptyPredicate.apply(request, filter);

        // Assert
        assertTrue(result instanceof NotApplicable);
    }

    @Test
    void testIsDescriptionEmpty_ShouldReturnTrue_whenDoesNotMatch() {
        MentorshipRequest request = MentorshipRequest.builder().description("somthening").build();
        RequestFilter filter =  RequestFilter.builder().description("somthening").build();

        // Act
        PredicateResult result = predicates.isDescriptionEmptyPredicate.apply(request, filter);

        // Assert
        assertTrue(result instanceof PredicateTrue);
    }

}
