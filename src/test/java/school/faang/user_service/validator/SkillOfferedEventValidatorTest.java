package school.faang.user_service.validator;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventSkillOfferedDto;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SkillOfferedEventValidatorTest {
    @Mock
    private SkillOfferRepository skillOfferRepository;

    @InjectMocks
    private SkillOfferedEventValidator skillOfferedEventValidator;

    @Test
    void validate_ValidDto_NoExceptionThrown() {
        EventSkillOfferedDto validDto = new EventSkillOfferedDto();
        validDto.setAuthorId(1L);
        validDto.setReceiverId(2L);
        validDto.setSkillOfferedId(3L);

        when(skillOfferRepository.existsById(validDto.getSkillOfferedId())).thenReturn(true);

        skillOfferedEventValidator.validate(validDto);

        verify(skillOfferRepository).existsById(validDto.getSkillOfferedId());
    }

    @Test
    void validate_NullDto_ThrowsDataValidationException() {
        EventSkillOfferedDto nullDto = null;

        assertThrows(DataValidationException.class, () -> skillOfferedEventValidator.validate(nullDto));
    }

    @Test
    void validate_InvalidAuthorId() {
        EventSkillOfferedDto invalidAuthorIdDto = new EventSkillOfferedDto();
        invalidAuthorIdDto.setAuthorId(0L);

        assertThrows(DataValidationException.class, () -> skillOfferedEventValidator.validate(invalidAuthorIdDto));
    }

    @Test
    void validate_InvalidReceiverId() {
        EventSkillOfferedDto invalidReceiverIdDto = new EventSkillOfferedDto();
        invalidReceiverIdDto.setReceiverId(0L);

        assertThrows(DataValidationException.class, () -> skillOfferedEventValidator.validate(invalidReceiverIdDto));
    }

    @Test
    void validate_InvalidSkillOfferedId() {
        EventSkillOfferedDto invalidSkillOfferedIdDto = new EventSkillOfferedDto();
        invalidSkillOfferedIdDto.setSkillOfferedId(0L);

        assertThrows(DataValidationException.class, () -> skillOfferedEventValidator.validate(invalidSkillOfferedIdDto));
    }

    @Test
    void validate_SameAuthorAndReceiver() {
        EventSkillOfferedDto sameAuthorAndReceiverDto = new EventSkillOfferedDto();
        sameAuthorAndReceiverDto.setAuthorId(1L);
        sameAuthorAndReceiverDto.setReceiverId(1L);

        assertThrows(DataValidationException.class, () -> skillOfferedEventValidator.validate(sameAuthorAndReceiverDto));
    }

    @Test
    void validate_SkillDoesNotExist_ThrowsDataValidationException() {
        EventSkillOfferedDto skillDoesNotExistDto = new EventSkillOfferedDto();
        skillDoesNotExistDto.setSkillOfferedId(3L);

        doReturn(false).when(skillOfferRepository).existsById(skillDoesNotExistDto.getSkillOfferedId());

        assertThrows(DataValidationException.class, () -> skillOfferedEventValidator.validate(skillDoesNotExistDto));
    }
}