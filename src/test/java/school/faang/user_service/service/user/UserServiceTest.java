package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    PromotionRepository promotionRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Mock
    private List<UserFilter> userFilters;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
    }

    @Test
    @DisplayName("Should return users in correct order when users are found and filtered")
    public void testGetFilteredUsers_FoundAndFiltered() {
        Country usa = new Country(1, "USA", List.of());
        Country uk = new Country(2, "UK", List.of());

        User callingUser = new User();
        callingUser.setId(1L);
        callingUser.setUsername("John Doe");
        callingUser.setCountry(usa);

        User promoted1 = new User();
        promoted1.setId(2L);
        promoted1.setUsername("John Smith");
        promoted1.setCountry(uk);

        Promotion promotion1_1 = new Promotion();
        promotion1_1.setPromotionTarget("profile");
        promotion1_1.setRemainingShows(5);
        promotion1_1.setPriorityLevel(3);

        Promotion promotion1_2 = new Promotion();
        promotion1_2.setPromotionTarget("event");
        promotion1_2.setRemainingShows(2);
        promotion1_2.setPriorityLevel(1);

        promoted1.setPromotions(new ArrayList<>(List.of(promotion1_1, promotion1_2)));

        User promoted2 = new User();
        promoted2.setId(3L);
        promoted2.setUsername("John Smith");
        promoted2.setCountry(usa);
        promoted2.setPromotions(new ArrayList<>());

        Promotion promotion2_1 = new Promotion();
        promotion2_1.setPromotionTarget("profile");
        promotion2_1.setRemainingShows(5);
        promotion2_1.setPriorityLevel(3);

        Promotion promotion2_2 = new Promotion();
        promotion2_2.setPromotionTarget("event");
        promotion2_2.setRemainingShows(3);
        promotion2_2.setPriorityLevel(2);

        promoted2.setPromotions(new ArrayList<>(List.of(promotion2_1, promotion2_2)));

        List<UserFilter> filters = new ArrayList<>();
        filters.add(new UserNameFilter());

        UserFilterDto filterDto = new UserFilterDto();
        filterDto.setNamePattern("John");

        List<User> filteredUsers = List.of(callingUser, promoted1, promoted2);

        when(userFilters.stream()).thenReturn(filters.stream());
        when(userRepository.findById(callingUser.getId())).thenReturn(Optional.of(callingUser));
        when(userRepository.findAll(ArgumentMatchers.<Specification<User>>any())).thenReturn(filteredUsers);

        List<UserDto> result = userService.getFilteredUsers(filterDto, callingUser.getId());

        verify(userMapper).toDto(callingUser);
        verify(userMapper).toDto(promoted1);
        verify(userMapper).toDto(promoted2);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(3, result.size()),
                () -> assertEquals(3L, result.get(0).getId()),
                () -> assertEquals(2L, result.get(1).getId()),
                () -> assertEquals(1L, result.get(2).getId())
        );
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when calling user is not found")
    public void testGetFilteredUsers_CallingUserNotFound() {
        UserFilterDto filterDto = new UserFilterDto();

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.getFilteredUsers(filterDto, 1L));
    }
}
