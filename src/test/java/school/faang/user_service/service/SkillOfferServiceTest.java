package school.faang.user_service.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import school.faang.user_service.dto.skill.SkillOfferDto;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.skill.SkillOfferMapperImpl;
import school.faang.user_service.publisher.EventSkillOfferedPublisher;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.SkillOfferValidator;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SkillOfferServiceTest {
    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Spy
    private SkillOfferMapperImpl skillOfferMapper;

    @Mock
    private EventSkillOfferedPublisher skillOfferedPublisher;

    @Mock
    private SkillOfferValidator validator;

    @InjectMocks
    private SkillOfferService skillOfferService;
    private SkillOfferDto dto;
    private SkillOffer offer;

    @BeforeEach
    void setUp() {
        dto = SkillOfferDto.builder()
                .id(1L)
                .skill(2L)
                .authorId(3L)
                .receiverId(4L)
                .build();

        offer = SkillOffer.builder()
                .id(1L)
                .build();
    }

    @Test
    void testCreateSkillOffer_ValidDto() {
        when(skillOfferMapper.toEntity(dto)).thenReturn(offer);
        when(skillOfferRepository.existsById(1L)).thenReturn(false);
        when(skillOfferMapper.toDto(offer)).thenReturn(dto);

        when(skillOfferMapper.toDto(any())).thenReturn(dto);

        SkillOfferDto result = skillOfferService.createSkillOffer(dto);

        assertNotNull(result);
        assertEquals(dto, result);
        verify(validator).validate(dto);
        verify(skillOfferRepository).existsById(1L);
        verify(skillOfferRepository).save(offer);
        verify(skillOfferedPublisher).publish(dto);
    }

    @Test
    void testCreateSkillOffer_DuplicateId() {
        when(skillOfferRepository.existsById(1L)).thenReturn(true);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> skillOfferService.createSkillOffer(dto));
        assertEquals("Such skill offer already exists", exception.getMessage());
        verify(validator).validate(dto);
        verify(skillOfferRepository, never()).save(any());
        verify(skillOfferedPublisher, never()).publish(any());
    }
}