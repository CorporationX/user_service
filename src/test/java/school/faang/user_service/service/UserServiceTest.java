package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserNameFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.PromotionRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private User callingUser;
    private UserDto callingUserDto;
    private User userWithPromotion;
    private UserDto userWithPromotionDto;
    private UserFilterDto filterDto;
    private Country usa;
    private List<UserFilter> filters;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PromotionRepository promotionRepository;

    @Mock
    private List<UserFilter> userFilters;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        usa = new Country(1, "USA", List.of());

        callingUser = new User();
        callingUser.setId(1L);
        callingUser.setUsername("John Doe");
        callingUser.setCountry(usa);
        callingUserDto = new UserDto();
        callingUserDto.setId(1L);

        userWithPromotion = new User();
        userWithPromotion.setId(2L);
        userWithPromotion.setUsername("John Smith");
        userWithPromotion.setPromotion(new Promotion());
        userWithPromotion.getPromotion().setPromotionTarget("profile");
        userWithPromotion.getPromotion().setRemainingShows(5);
        userWithPromotion.getPromotion().setPriorityLevel(3);
        userWithPromotion.setCountry(usa);
        userWithPromotionDto = new UserDto();
        userWithPromotionDto.setId(2L);

        filterDto = new UserFilterDto();
        filters = new ArrayList<>();
        filters.add(new UserNameFilter());
    }

    @Test
    public void testGetFilteredUsers_UserFound_Success() {

        UserFilterDto filterDto = new UserFilterDto();
        filterDto.setNamePattern("John");

        List<User> filteredUsers = List.of(callingUser, userWithPromotion);

        when(userFilters.stream()).thenReturn(filters.stream());
        when(userRepository.findById(callingUser.getId())).thenReturn(java.util.Optional.of(callingUser));
        when(userRepository.findAll(any(Specification.class))).thenReturn(filteredUsers);
        when(mapper.toUserDto(callingUser)).thenReturn(callingUserDto);
        when(mapper.toUserDto(userWithPromotion)).thenReturn(userWithPromotionDto);

        List<UserDto> result = userService.getFilteredUsers(filterDto, callingUser.getId());

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2L, result.get(0).getId());
        assertEquals(1L, result.get(1).getId());
    }

    @Test
    public void testGetFilteredUsers_UserNotFound() {
        when(userRepository.findById(callingUser.getId())).thenReturn(java.util.Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.getFilteredUsers(filterDto, callingUser.getId()));
    }

    @Test
    public void testGetFilteredUsers_NoExpiredPromotions() {
        List<User> filteredUsers = List.of(userWithPromotion);
        List<User> priorityFilteredUsers = List.of(userWithPromotion);

        when(userRepository.findById(callingUser.getId())).thenReturn(java.util.Optional.of(callingUser));
        when(userFilters.stream()).thenReturn(filters.stream());
        when(userRepository.findAll(any(Specification.class))).thenReturn(filteredUsers);
        when(mapper.toUserDto(userWithPromotion)).thenReturn(new UserDto());

        List<UserDto> result = userService.getFilteredUsers(filterDto, callingUser.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(promotionRepository, times(1)).decreaseRemainingShows(anyList(), eq("profile"));
        verify(promotionRepository, never()).deleteAll(anyList()); // No expired promotions
    }

    @Test
    public void testGetFilteredUsersFromRepository_FilterApplies_Success() {
        UserFilterDto filterDto = new UserFilterDto();
        filterDto.setNamePattern("John");

        when(userFilters.stream()).thenReturn(filters.stream());
        when(userRepository.findAll(any(Specification.class))).thenReturn(List.of(callingUser, userWithPromotion));

        List<User> result = userService.getFilteredUsersFromRepository(filterDto);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getUsername());
        assertEquals("John Smith", result.get(1).getUsername());
    }
}
