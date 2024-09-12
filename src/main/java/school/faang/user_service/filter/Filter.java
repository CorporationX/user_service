package school.faang.user_service.filter;

public interface Filter<E, F> {

    boolean isApplicable(F filter);

    boolean apply(E entity, F filter);
}
