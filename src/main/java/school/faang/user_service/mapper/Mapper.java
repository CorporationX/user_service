package school.faang.user_service.mapper;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface Mapper<F, T> {

    T map(F object);

    default T map(F fromObject, T toObject) {
        return toObject;
    }

    default <V, E> V getField(E object, Function<E, V> function, V orElse) {
        return Optional.ofNullable(object).map(function::apply).orElse(orElse);
    }

    default <V, E> V getField(E object, Function<E, V> function) {
        return getField(object, function, null);
    }

    default <E, D> D getDto(E object, Mapper<E, D> maper) {
        return Optional.ofNullable(object).map(maper::map).orElse(null);
    }

    default <R extends JpaRepository<E, I>, E, I> E getEntity(I id, R repository) {
        return Optional.ofNullable(id).flatMap(repository::findById).orElse(null);
    }

    default <R extends JpaRepository<E, I>, E, I> List<E> getEntities(Iterable<I> ids, R repository) {
        return Optional.ofNullable(ids).map(repository::findAllById).orElse(null);
    }

}