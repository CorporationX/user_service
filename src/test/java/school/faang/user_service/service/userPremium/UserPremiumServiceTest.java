package school.faang.user_service.service.userPremium;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.userPremium.UserPremiumDto;
import school.faang.user_service.dto.userPremium.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.userPremium.UserPremiumMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.filter.userPremium.UserPremiumFilter;
import school.faang.user_service.service.userPremium.UserPremiumService;

import java.util.List;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class UserPremiumServiceTest {
    @InjectMocks
    private UserPremiumService userPremiumService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserPremiumMapper userPremiumMapper;
    @Mock
    private List<UserPremiumFilter> userFilters;
    @Mock
    private UserPremiumFilter usernameFilterPattern;
    @Mock
    private UserPremiumFilter countryFilterPattern;

    @Test
    void testGetPremiumUsers() {
        Mockito.when(userRepository.findPremiumUsers()).thenReturn(Stream.of(new User()));
        Mockito.when(userFilters.stream()).thenReturn(Stream.of(usernameFilterPattern, countryFilterPattern));

        Mockito.when(usernameFilterPattern.isApplication(Mockito.any())).thenReturn(true);
        Mockito.when(usernameFilterPattern.apply(Mockito.any(), Mockito.any())).thenReturn(Stream.of(new User()));
        Mockito.when(countryFilterPattern.isApplication(Mockito.any())).thenReturn(true);
        Mockito.when(countryFilterPattern.apply(Mockito.any(), Mockito.any())).thenReturn(Stream.of(new User()));
        Mockito.when(userPremiumMapper.toDto(Mockito.any())).thenReturn(new UserPremiumDto());

        userPremiumService.getPremiumUsers(new UserFilterDto());
        Mockito.verify(userPremiumMapper, Mockito.times(1)).toDto(Mockito.any());
    }
}