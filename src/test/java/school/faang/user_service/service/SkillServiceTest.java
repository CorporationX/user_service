package school.faang.user_service.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.NotEnoughOffersException;
import school.faang.user_service.exception.SkillNotFoundException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapperImpl;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillOfferServiceImpl skillOfferService;

    @Mock
    private UserSkillGuaranteeServiceImpl userSkillGuaranteeService;

    @Spy
    private SkillMapperImpl skillMapper;

    @InjectMocks
    private SkillServiceImpl skillService;


    @Test
    public void testCreateWithExistingTitle() {
        SkillDto skillDto = new SkillDto(null,"Java");
        when(skillRepository.existsByTitle(skillDto.title())).thenReturn(true);
        assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    public void testCreate() {
        SkillDto skillDto = new SkillDto(null,"Java");
        when(skillRepository.existsByTitle(skillDto.title())).thenReturn(false);
        SkillDto result = skillService.create(skillDto);
        verify(skillMapper, times(1)).toEntity(skillDto);
        verify(skillRepository, times(1)).save(any(Skill.class));
        assertEquals("Java", result.title());
    }

    @Test
    public void testGetUserSkills() {
        long userId = 1L;
        Skill skill1 = Skill.builder().id(1).title("Java").build();
        Skill skill2 = Skill.builder().id(2).title("Python").build();
        Skill skill3 = Skill.builder().id(3).title("JavaScript").build();
        List<Skill> skills = List.of(skill1, skill2, skill3);
        when(skillRepository.findAllByUserId(userId)).thenReturn(skills);
        List<SkillDto> result = skillService.getUserSkills(1L);
        verify(skillRepository, times(1)).findAllByUserId(userId);
        assertEquals(3, result.size());

    }

    @Test
    public void testGetOfferedSkills() {
        long userId = 1L;
        Skill skill1 = Skill.builder().id(1L).title("Java").build();
        Skill skill2 = Skill.builder().id(2L).title("Python").build();
        Skill skill3 = Skill.builder().id(1L).title("Java").build();

        List<Skill> offeredSkills = List.of(skill1, skill2, skill3);

        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(offeredSkills);

        List<SkillCandidateDto> result = skillService.getOfferedSkills(userId);

        assertEquals(2, result.size());

        Map<Long, Long> expectedCounts = Map.of(1L, 2L, 2L, 1L);

        for (SkillCandidateDto dto : result) {
            assertTrue(expectedCounts.containsKey(dto.skill().id()));
            assertEquals(expectedCounts.get(dto.skill().id()), dto.offersAmount());
        }

        verify(skillRepository, times(1)).findSkillsOfferedToUser(userId);

        verify(skillMapper, times(2)).toDto(any(Skill.class));
    }

    @Test
    public void testAcquireSkillFromOffersWithExistingSkill() {
        long userId = 1L;
        long skillId = 2L;
        Skill existingSkill = new Skill();
        existingSkill.setTitle("Java");
        existingSkill.setId(1L);

        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.of(existingSkill));
        assertThrows(DataValidationException.class, () -> skillService.acquireSkillFromOffers(skillId, userId));
    }

    @Test
    public void testAcquireSkillFromOffersWithNonExistingSkill() {

        long userId = 1L;
        long skillId = 2L;
        Skill newSkill = Skill.builder().id(skillId).title("Java").build();
        when(skillRepository.findUserSkill(skillId, userId))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(newSkill));
        doNothing().when(skillOfferService).isEnoughAmountOffersToSkill(skillId, userId);
        doNothing().when(skillRepository).assignSkillToUser(skillId, userId);
        doNothing().when(userSkillGuaranteeService).addUserSkillGuarantee(skillId, userId);
        SkillDto skillDto = skillService.acquireSkillFromOffers(skillId, userId);
        verify(skillOfferService, times(1)).isEnoughAmountOffersToSkill(skillId, userId);
        verify(skillRepository, times(1)).assignSkillToUser(skillId, userId);
        verify(userSkillGuaranteeService, times(1)).addUserSkillGuarantee(skillId, userId);
        verify(skillMapper, times(1)).toDto(newSkill);
        assertEquals("Java", skillDto.title());
    }
    @Nested
    class DoesSkillExistTests {

        @Test
        void shouldReturnTrueWhenSkillExists() {
            long skillId = 1L;
            when(skillRepository.existsById(skillId)).thenReturn(true);

            boolean result = skillService.doesSkillExists(skillId);

            verify(skillRepository).existsById(skillId);
            assertTrue(result);
        }

        @Test
        void shouldReturnFalseWhenSkillDoesNotExist() {
            long skillId = 999L;
            when(skillRepository.existsById(skillId)).thenReturn(false);

            boolean result = skillService.doesSkillExists(skillId);

            verify(skillRepository).existsById(skillId);
            assertFalse(result);
        }
    }

    @Nested
    class FindSkillsByUserIdTests {

        @Test
        void shouldReturnSkillsWhenUserHasSkills() {
            long userId = 1L;
            List<Skill> expectedSkills = List.of(
                    Skill.builder().id(1L).title("Java").build(),
                    Skill.builder().id(2L).title("Spring").build()
            );

            when(skillRepository.findAllByUserId(userId)).thenReturn(expectedSkills);

            List<Skill> result = skillService.findSkillsByUserId(userId);

            assertEquals(expectedSkills, result);
            verify(skillRepository).findAllByUserId(userId);
        }

        @Test
        void shouldReturnEmptyListWhenUserHasNoSkills() {
            long userId = 999L;
            when(skillRepository.findAllByUserId(userId)).thenReturn(List.of());

            List<Skill> result = skillService.findSkillsByUserId(userId);

            assertTrue(result.isEmpty());
            verify(skillRepository).findAllByUserId(userId);
        }
    }

    @Nested
    class createTests {

        @Test
        void shouldSaveAndReturnDtoWhenCreateSkillWithUniqueTitle() {
            SkillDto inputDto = new SkillDto(null, "Java");
            Skill entity = Skill.builder().title("Java").build();
            Skill savedEntity = Skill.builder().id(1L).title("Java").build();
            SkillDto expectedDto = new SkillDto(1L, "Java");

            when(skillRepository.existsByTitle("Java")).thenReturn(false);
            when(skillMapper.toEntity(inputDto)).thenReturn(entity);
            when(skillRepository.save(entity)).thenReturn(savedEntity);

            when(skillMapper.toDto(any(Skill.class))).thenReturn(expectedDto);

            SkillDto result = skillService.create(inputDto);

            assertEquals(expectedDto, result);
            verify(skillRepository).save(entity);

            verify(skillMapper).toDto(any(Skill.class));
        }

        @Test
        void shouldThrowExceptionWhenSkillTitleExist() {
            SkillDto inputDto = new SkillDto(null, "Java");

            when(skillRepository.existsByTitle("Java")).thenReturn(true);

            assertThrows(DataValidationException.class, () -> skillService.create(inputDto));
            verify(skillRepository, never()).save(any());
        }
    }

    @Nested
    class getUserSkillsTests {

        @Test
        void shouldReturnsListOfSkillDtoWhenUserHasSkills() {
            // Arrange
            long userId = 1L;
            List<Skill> skills = List.of(
                    Skill.builder().id(1L).title("Java").build(),
                    Skill.builder().id(2L).title("Spring").build()
            );
            List<SkillDto> expectedDtos = List.of(
                    new SkillDto(1L, "Java"),
                    new SkillDto(2L, "Spring")
            );

            // Act
            when(skillRepository.findAllByUserId(userId)).thenReturn(skills);
            when(skillMapper.toDto(skills.get(0))).thenReturn(expectedDtos.get(0));
            when(skillMapper.toDto(skills.get(1))).thenReturn(expectedDtos.get(1));

            List<SkillDto> result = skillService.getUserSkills(userId);

            // Assert
            assertEquals(expectedDtos.size(), result.size());
            assertEquals(expectedDtos, result);
            verify(skillRepository).findAllByUserId(userId);
        }

        @Test
        void shouldReturnsEmptyListWhenUserHasNoSkills() {
            long userId = 999L;
            when(skillRepository.findAllByUserId(userId)).thenReturn(List.of());

            List<SkillDto> result = skillService.getUserSkills(userId);

            assertTrue(result.isEmpty());
            verify(skillRepository).findAllByUserId(userId);
        }

        @Test
        void shouldCallRepositoryWithCorrectUserIdWhenGetUserSkills() {
            long userId = 42L;
            when(skillRepository.findAllByUserId(userId)).thenReturn(List.of());

            skillService.getUserSkills(userId);

            verify(skillRepository).findAllByUserId(userId);
        }
    }

    @Nested
    class getOfferedSkillsTests {

        @Test
        void shouldReturnEmptyListWhenNoOfferedSkills() {
            long userId = 1L;
            when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(List.of());

            List<SkillCandidateDto> result = skillService.getOfferedSkills(userId);

            assertTrue(result.isEmpty());
            verify(skillRepository).findSkillsOfferedToUser(userId);
        }

        @Test
        void shouldReturnDtoWithCountWhenSingleSkillMultipleOffers() {
            long userId = 1L;
            Skill skill = Skill.builder().id(100L).title("Java").build();
            SkillDto skillDto = new SkillDto(100L, "Java");

            when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(List.of(skill, skill, skill));
            when(skillMapper.toDto(skill)).thenReturn(skillDto);

            List<SkillCandidateDto> result = skillService.getOfferedSkills(userId);

            assertEquals(1, result.size());
            assertEquals(3, result.get(0).offersAmount());
            assertEquals("Java", result.get(0).skill().title());
            verify(skillMapper, times(1)).toDto(skill);
        }

        @Test
        void shouldReturnCorrectCountsWhenOfferedMultipleSkills() {
            long userId = 1L;
            Skill skill1 = Skill.builder().id(100L).title("Java").build();
            Skill skill2 = Skill.builder().id(200L).title("Spring").build();
            SkillDto dto1 = new SkillDto(100L, "Java");
            SkillDto dto2 = new SkillDto(200L, "Spring");

            when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(List.of(skill1, skill1, skill2));
            when(skillMapper.toDto(skill1)).thenReturn(dto1);
            when(skillMapper.toDto(skill2)).thenReturn(dto2);

            List<SkillCandidateDto> result = skillService.getOfferedSkills(userId);

            assertEquals(2, result.size());

            Map<Long, SkillCandidateDto> resultMap = result.stream()
                    .collect(Collectors.toMap(
                            dto -> dto.skill().id(),
                            dto -> dto
                    ));

            SkillCandidateDto dtoForSkill1 = resultMap.get(100L);
            assertNotNull(dtoForSkill1);
            assertEquals(2, dtoForSkill1.offersAmount());
            assertEquals("Java", dtoForSkill1.skill().title());

            SkillCandidateDto dtoForSkill2 = resultMap.get(200L);
            assertNotNull(dtoForSkill2);
            assertEquals(1, dtoForSkill2.offersAmount());
            assertEquals("Spring", dtoForSkill2.skill().title());
        }

        @Test
        void shouldReturnsListOfSkillCandidateDtoWhenGetOfferedSkills() {
            long userId = 1L;
            Skill skill1 = Skill.builder().id(1L).title("Java").build();
            Skill skill2 = Skill.builder().id(2L).title("Spring").build();
            List<Skill> offeredSkills = List.of(skill1, skill1, skill2);
            SkillDto dto1 = new SkillDto(1L, "Java");
            SkillDto dto2 = new SkillDto(2L, "Spring");

            when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(offeredSkills);
            when(skillMapper.toDto(skill1)).thenReturn(dto1);
            when(skillMapper.toDto(skill2)).thenReturn(dto2);

            List<SkillCandidateDto> result = skillService.getOfferedSkills(userId);

            assertEquals(2, result.size());
            assertEquals(2L, result.get(0).offersAmount());
            assertEquals(1L, result.get(1).offersAmount());
            assertEquals(dto1, result.get(0).skill());
            assertEquals(dto2, result.get(1).skill());
        }

        @Test
        void shouldHandleMultipleCallsToFindSkillByIdWhenGetOfferedSkills() {
            long userId = 1L;
            Skill skill1 = Skill.builder().id(1L).title("Java").build();
            Skill skill2 = Skill.builder().id(2L).title("Spring").build();
            List<Skill> skills = List.of(skill1, skill1, skill2);

            when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(skills);
            when(skillMapper.toDto(skill1)).thenReturn(new SkillDto(1L, "Java"));
            when(skillMapper.toDto(skill2)).thenReturn(new SkillDto(2L, "Spring"));

            List<SkillCandidateDto> result = skillService.getOfferedSkills(userId);

            assertEquals(2, result.size());
        }
    }

    @Nested
    class acquireSkillFromOffersTests {

        @Test
        void shouldReturnSkillDtoWhenAcquireSkillFromOffers() {
            // Arrange
            long userId = 1L;
            long skillId = 100L;
            Skill skill = Skill.builder().id(skillId).title("Java").build();
            SkillDto expectedDto = new SkillDto(skillId, "Java");

            when(skillRepository.findUserSkill(skillId, userId))
                    .thenReturn(Optional.empty())
                    .thenReturn(Optional.of(skill));
            doNothing().when(skillOfferService).isEnoughAmountOffersToSkill(skillId, userId);
            when(skillMapper.toDto(skill)).thenReturn(expectedDto);

            // Act
            SkillDto result = skillService.acquireSkillFromOffers(skillId, userId);

            // Assert
            verify(skillRepository).assignSkillToUser(skillId, userId);
            verify(userSkillGuaranteeService).addUserSkillGuarantee(skillId, userId);
            assertEquals(expectedDto, result);
        }

        @Test
        void shouldThrowsExceptionWhenSkillAlreadyExists() {
            // Arrange
            long userId = 1L;
            long skillId = 100L;
            Skill existingSkill = Skill.builder().id(skillId).title("Java").build();

            // Act
            when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.of(existingSkill));

            // Assert
            assertThrows(DataValidationException.class,
                    () -> skillService.acquireSkillFromOffers(skillId, userId));

            verify(skillRepository, never()).assignSkillToUser(anyLong(), anyLong());
            verify(userSkillGuaranteeService, never()).addUserSkillGuarantee(anyLong(), anyLong());
        }

        @Test
        void shouldThrowsExceptionWhenNotEnoughOffers() {
            // Arrange
            long userId = 1L;
            long skillId = 100L;

            // Act
            when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());
            doThrow(new NotEnoughOffersException("Not enough offers"))
                    .when(skillOfferService).isEnoughAmountOffersToSkill(skillId, userId);

            // Assert
            assertThrows(NotEnoughOffersException.class,
                    () -> skillService.acquireSkillFromOffers(skillId, userId));

            verify(skillRepository, never()).assignSkillToUser(anyLong(), anyLong());
            verify(userSkillGuaranteeService, never()).addUserSkillGuarantee(anyLong(), anyLong());
        }

        @Test
        void shouldThrowsExceptionWhenSkillNotFoundAfterAdding() {
            // Arrange
            long userId = 1L;
            long skillId = 100L;

            // Act
            when(skillRepository.findUserSkill(skillId, userId))
                    .thenReturn(Optional.empty())
                    .thenReturn(Optional.empty());

            doNothing().when(skillOfferService).isEnoughAmountOffersToSkill(skillId, userId);

            // Assert
            assertThrows(SkillNotFoundException.class,
                    () -> skillService.acquireSkillFromOffers(skillId, userId));

            verify(skillRepository).assignSkillToUser(skillId, userId);
            verify(userSkillGuaranteeService).addUserSkillGuarantee(skillId, userId);
        }

        @Test
        void shouldCallRepositoryAndGuaranteeServiceWhenAcquireSkillFromOffers() {
            long skillId = 1L;
            long userId = 1L;
            Skill skill = new Skill();
            skill.setId(skillId);

            when(skillRepository.findUserSkill(skillId, userId))
                    .thenReturn(Optional.empty())
                    .thenReturn(Optional.of(skill));
            doNothing().when(skillOfferService).isEnoughAmountOffersToSkill(skillId, userId);
            when(skillMapper.toDto(skill)).thenReturn(new SkillDto(skillId, "Java"));

            skillService.acquireSkillFromOffers(skillId, userId);

            verify(skillRepository).assignSkillToUser(skillId, userId);
            verify(userSkillGuaranteeService).addUserSkillGuarantee(skillId, userId);
        }
    }

    @Nested
    class findSkillByIdTests {

        @Test
        void shouldReturnsSkillWhenSkillExists() {
            long skillId = 100L;
            Skill expectedSkill = Skill.builder().id(skillId).title("Java").build();
            List<Skill> skills = List.of(expectedSkill, Skill.builder().id(200L).title("Spring").build());

            Optional<Skill> result = skillService.findSkillById(skillId, skills);

            assertTrue(result.isPresent());
            assertEquals(expectedSkill, result.get());
        }

        @Test
        void shouldReturnsEmptyWhenSkillNotExists() {
            long skillId = 999L;
            List<Skill> skills = List.of(
                    Skill.builder().id(100L).title("Java").build(),
                    Skill.builder().id(200L).title("Spring").build()
            );

            Optional<Skill> result = skillService.findSkillById(skillId, skills);

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    class toSkillCandidateDtoTests {

        @Test
        void shouldReturnsDtoWhenSkillExists() {
            Long skillId = 100L;
            Skill skill = Skill.builder().id(skillId).title("Java").build();
            List<Skill> skills = List.of(skill);
            Map.Entry<Long, Long> entry = Map.entry(skillId, 3L);

            SkillDto skillDto = new SkillDto(skillId, "Java");
            when(skillMapper.toDto(skill)).thenReturn(skillDto);

            SkillCandidateDto dto = skillService.toSkillCandidateDto(entry, skills);

            assertEquals(skillId, dto.skill().id());
            assertEquals("Java", dto.skill().title());
            assertEquals(3L, dto.offersAmount());
        }

        @Test
        void shouldThrowsExceptionWhenSkillNotExists() {
            Long skillId = 999L;
            List<Skill> skills = List.of(Skill.builder().id(100L).title("Java").build());
            Map.Entry<Long, Long> entry = Map.entry(skillId, 3L);

            assertThrows(SkillNotFoundException.class,
                    () -> skillService.toSkillCandidateDto(entry, skills));
        }
    }
}