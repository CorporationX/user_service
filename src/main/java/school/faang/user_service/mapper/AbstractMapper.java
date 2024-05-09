package school.faang.user_service.mapper;

public interface AbstractMapper<E, D>{

    E toEntity(D dto);

    D toDto(E entity);

}
