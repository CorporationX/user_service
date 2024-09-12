package school.faang.user_service.exception;

import static school.faang.user_service.exception.constants.ErrorMessage.RESOURCE_NOT_FOUND;

public class ResourceNotFoundException extends ApplicationException {
    public ResourceNotFoundException(String resourceName, Object resourceId) {
        super(String.format(RESOURCE_NOT_FOUND, resourceName, resourceId));
    }
}
