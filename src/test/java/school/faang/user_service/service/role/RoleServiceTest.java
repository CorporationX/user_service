package school.faang.user_service.service.role;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Role;
import school.faang.user_service.model.user.RoleThesaurus;
import school.faang.user_service.repository.role.RoleRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    @Test
    void getByNameOrThrow_shouldReturnRole() {
        RoleThesaurus name = RoleThesaurus.OWNER;
        Role expectedRole = new Role();
        expectedRole.setName(name);

        Mockito.when(roleRepository.findByName(name)).thenReturn(Optional.of(expectedRole));

        Role result = roleService.getByNameOrThrow(name);

        assertThat(result).isEqualTo(expectedRole);
    }

    @Test
    void getByNameOrThrow_shouldThrowIfRoleNotFound() {
        RoleThesaurus name = RoleThesaurus.ATTENDEE;

        Mockito.when(roleRepository.findByName(name)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleService.getByNameOrThrow(name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Role not found");
    }
}
