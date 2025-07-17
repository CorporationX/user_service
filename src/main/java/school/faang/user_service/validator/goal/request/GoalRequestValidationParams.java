package school.faang.user_service.validator.goal.request;

import school.faang.user_service.util.Helper;

import java.io.Serializable;

public record GoalRequestValidationParams(String path) implements Serializable {
    public String path(String path) {
        return Helper.createPath(this.path, path);
    }
}
