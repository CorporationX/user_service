package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import school.faang.user_service.dto.skill.SkillOfferDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SkillOfferValidatorTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @InjectMocks
    private SkillOfferValidator skillValidator;

    @Test
    void validate_ValidDto_NoExceptionThrown() {
        SkillOfferDto validDto = new SkillOfferDto();
        validDto.setAuthorId(1L);
        validDto.setSkill(3L);

        when(skillOfferRepository.existsById(validDto.getSkill())).thenReturn(true);

        skillValidator.validate(validDto);

        verify(skillOfferRepository).existsById(validDto.getSkill());
    }

    @Test
    void validate_InvalidAuthorId() {
        SkillOfferDto invalidAuthorIdDto = new SkillOfferDto();
        invalidAuthorIdDto.setAuthorId(null);

        assertThrows(DataValidationException.class, () -> skillValidator.validate(invalidAuthorIdDto));
    }

    @Test
    void validate_InvalidReceiverId() {
        SkillOfferDto invalidReceiverIdDto = new SkillOfferDto();
        invalidReceiverIdDto.setReceiverId(0L);

        assertThrows(DataValidationException.class, () -> skillValidator.validate(invalidReceiverIdDto));
    }

    @Test
    void validate_InvalidSkillOfferedId() {
        SkillOfferDto invalidSkillOfferedIdDto = new SkillOfferDto();
        invalidSkillOfferedIdDto.setSkill(0L);

        assertThrows(DataValidationException.class, () -> skillValidator.validate(invalidSkillOfferedIdDto));
    }

    @Test
    void validate_SameAuthorAndReceiver() {
        SkillOfferDto sameAuthorAndReceiverDto = new SkillOfferDto();
        sameAuthorAndReceiverDto.setAuthorId(1L);
        sameAuthorAndReceiverDto.setReceiverId(1L);

        assertThrows(DataValidationException.class, () -> skillValidator.validate(sameAuthorAndReceiverDto));
    }

    @Test
    void validate_SkillDoesNotExist_ThrowsDataValidationException() {
        SkillOfferDto skillDoesNotExistDto = new SkillOfferDto();
        skillDoesNotExistDto.setSkill(3L);

        doReturn(false).when(skillOfferRepository).existsById(skillDoesNotExistDto.getSkill());

        assertThrows(DataValidationException.class, () -> skillValidator.validate(skillDoesNotExistDto));
    }

    @Test
    void testValidate_NonExistingReceiver() {
        SkillOfferDto dto = SkillOfferDto.builder()
                .skill(1L)
                .receiverId(2L)
                .build();

        when(skillOfferRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(false);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> skillValidator.validate(dto));
        assertEquals("Selected receiver doesn't exist", exception.getMessage());
    }

    @Test
    void testValidate_ExistingSkillAndReceiver() {
        SkillOfferDto dto = SkillOfferDto.builder()
                .skill(1L)
                .receiverId(2L)
                .build();

        when(skillOfferRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);

        assertDoesNotThrow(() -> skillValidator.validate(dto));
    }
}