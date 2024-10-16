package school.faang.user_service.service.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import school.faang.user_service.model.dto.SkillCandidateDto;
import school.faang.user_service.model.dto.SkillDto;
import school.faang.user_service.model.entity.Skill;
import school.faang.user_service.model.entity.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.SkillAssignmentException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.model.event.SkillAcquiredEvent;
import school.faang.user_service.publisher.SkillAcquiredEventPublisher;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.model.entity.SkillOffer;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.SkillOfferRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillServiceImplTest {
    private final Long id = 1L;
    private SkillDto skillDto;
    private SkillDto anotherSkillDto;
    private Skill skillEntity;
    private Skill anotherSkillEntity;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    SkillOfferRepository skillOfferRepository;

    @Mock
    UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillMapper mapper;

    @Mock
    SkillAcquiredEventPublisher skillAcquiredEventPublisher;

    @InjectMocks
    private SkillServiceImpl skillService;

    @BeforeEach
    public void setUp() {
        skillDto = new SkillDto();
        skillDto.setId(null);
        skillDto.setTitle("New Skill");

        anotherSkillDto = new SkillDto();
        anotherSkillDto.setId(null);
        anotherSkillDto.setTitle("Yet Another Skill");

        skillEntity = new Skill();
        anotherSkillEntity = new Skill();
    }

    @AfterEach
    public void tearDown() {
        skillDto = anotherSkillDto = null;
        skillEntity = anotherSkillEntity = null;
    }

    @Test
    @DisplayName("Should return SkillDto with non-null ID when a new skill is successfully created")
    public void testCreateSkill_Success() {
        when(skillRepository.save(mapper.toSkillEntity(skillDto))).thenReturn(skillEntity);
        when(mapper.toSkillDto(skillEntity)).thenAnswer(invocation -> {
            SkillDto dtoWithNonNullId = skillDto;
            dtoWithNonNullId.setId(1L);
            return dtoWithNonNullId;
        });

        SkillDto resultDto = skillService.create(skillDto);

        assertAll(
                () -> assertEquals(1, resultDto.getId()),
                () -> assertEquals(skillDto.getTitle(), resultDto.getTitle())
        );

        verify(skillRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Should throw DataValidationException when skill with the same title already exists")
    public void testCreateSkill_ThrowsException() {
        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(true);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> skillService.create(skillDto));

        assertEquals("Skill with title 'New Skill' already exists.", exception.getMessage());

        verify(skillRepository, times(0)).save(any(Skill.class));
    }

    @Test
    @DisplayName("Should return a list of SkillDto when skills are available for the user")
    public void testGetUserSkills_WithSkills() {
        List<Skill> skills = List.of(skillEntity, anotherSkillEntity);
        when(skillRepository.findAllByUserId(anyLong())).thenReturn(skills);
        when(mapper.toListSkillDto(skills)).thenReturn(List.of(skillDto, anotherSkillDto));

        List<SkillDto> resultDtoList = skillService.getUserSkills(id);

        assertAll(
                () -> assertNotNull(resultDtoList),
                () -> assertEquals(2, resultDtoList.size()),
                () -> assertTrue(resultDtoList.contains(skillDto)),
                () -> assertTrue(resultDtoList.contains(anotherSkillDto))
        );

        verify(skillRepository, times(1)).findAllByUserId(id);
    }

    @Test
    @DisplayName("Should return an empty list when no skills are available for the user")
    public void testGetUserSkills_WithNoSkills() {
        when(skillRepository.findAllByUserId(anyLong())).thenReturn(Collections.emptyList());

        List<SkillDto> resultDtoList = skillService.getUserSkills(id);

        assertAll(
                () -> assertNotNull(resultDtoList),
                () -> assertTrue(resultDtoList.isEmpty())
        );

        verify(skillRepository, times(1)).findAllByUserId(id);
    }

    @Test
    @DisplayName("Should return offered skills when user exists")
    public void testGetOfferedSkills_Success() {
        skillEntity.setId(2L);
        skillEntity.setTitle("New Skill");
        anotherSkillEntity.setId(3L);
        anotherSkillEntity.setTitle("Yet Another Skill");

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(skillRepository.findSkillsOfferedToUser(anyLong())).thenReturn(List.of(skillEntity, anotherSkillEntity));
        when(mapper.toSkillDto(skillEntity)).thenReturn(skillDto);
        when(mapper.toSkillDto(anotherSkillEntity)).thenReturn(anotherSkillDto);

        List<SkillCandidateDto> result = skillService.getOfferedSkills(id);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(2, result.size()),
                () -> assertEquals(1L, result.get(0).getOffersAmount()),
                () -> assertEquals("New Skill", result.get(0).getSkill().getTitle())
        );

        verify(userRepository, times(1)).existsById(anyLong());
        verify(skillRepository, times(1)).findSkillsOfferedToUser(anyLong());
    }

    @Test
    @DisplayName("Should throw DataValidationException when user does not exist")
    public void testGetOfferedSkills_UserDoesNotExist() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> skillService.getOfferedSkills(108));

        assertEquals("User with id '108' does not exist.", exception.getMessage());

        verify(userRepository, times(1)).existsById(anyLong());
        verify(skillRepository, never()).findSkillsOfferedToUser(anyLong());
    }

    @Test
    @DisplayName("Should successfully acquire skill")
    public void testAcquireSkill_Success() {
        final AtomicInteger callCount = new AtomicInteger(0);
        when(skillRepository.findUserSkill(anyLong(), anyLong()))
                .thenAnswer((Answer<Optional<Skill>>) invocation -> {
                    if (callCount.incrementAndGet() == 1) {
                        return Optional.empty();
                    } else {
                        return Optional.of(skillEntity);
                    }
                });
        when(skillOfferRepository.findAllOffersOfSkill(anyLong(), anyLong()))
                .thenReturn(List.of(
                        new SkillOffer(1L, skillEntity, new Recommendation()),
                        new SkillOffer(2L, skillEntity, new Recommendation()),
                        new SkillOffer(3L, skillEntity, new Recommendation())
                ));
        doNothing().when(skillRepository).assignSkillToUser(anyLong(), anyLong());
        when(userSkillGuaranteeRepository.saveAll(anyList())).thenReturn(List.of());
        when(mapper.toSkillDto(any(Skill.class))).thenReturn(skillDto);

        doNothing().when(skillAcquiredEventPublisher).publish(any(SkillAcquiredEvent.class));

        SkillDto result = skillService.acquireSkillFromOffers(skillEntity.getId(), id);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals("New Skill", result.getTitle())
        );

        verify(userSkillGuaranteeRepository, times(1)).saveAll(anyList());
        verify(skillAcquiredEventPublisher, times(1)).
                publish(new SkillAcquiredEvent(id, skillEntity.getId()));
    }

    @Test
    @DisplayName("Should throw SkillAssignmentException when skill is already acquired")
    public void testAcquireSkill_AlreadyAcquired() {
        when(skillRepository.findUserSkill(anyLong(), anyLong())).thenReturn(Optional.of(skillEntity));

        SkillAssignmentException exception = assertThrows(SkillAssignmentException.class,
                () -> skillService.acquireSkillFromOffers(skillEntity.getId(), id));

        assertEquals("Skill already acquired by the user.", exception.getMessage());

        verify(skillOfferRepository, never()).findAllOffersOfSkill(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Should throw SkillAssignmentException when not enough skill offers")
    public void testAcquireSkill_NotEnoughOffers() {
        when(skillRepository.findUserSkill(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(skillOfferRepository.findAllOffersOfSkill(anyLong(), anyLong())).thenReturn(List.of());

        SkillAssignmentException exception = assertThrows(SkillAssignmentException.class,
                () -> skillService.acquireSkillFromOffers(skillEntity.getId(), id));

        assertEquals("Not enough skill offers to acquire this skill.", exception.getMessage());

        verify(skillRepository, never()).assignSkillToUser(anyLong(), anyLong());
    }
}
