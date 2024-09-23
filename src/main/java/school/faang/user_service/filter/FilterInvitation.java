package school.faang.user_service.filter;

import com.querydsl.core.types.dsl.BooleanExpression;

import java.util.Optional;

public interface FilterInvitation<T, U> {
    Optional<BooleanExpression> getCondition(T t, U u);
}