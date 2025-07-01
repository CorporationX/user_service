package school.faang.user_service.service.role;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Role;
import school.faang.user_service.model.user.RoleThesaurus;
import school.faang.user_service.repository.role.RoleRepository;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public Role getByNameOrThrow(RoleThesaurus name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));
    }
}
