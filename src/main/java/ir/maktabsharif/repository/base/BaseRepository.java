package ir.maktabsharif.repository.base;

import java.util.List;
import java.util.Optional;

public interface BaseRepository<T> {
    T create(T t);
    Optional<T> findById(Long id);
    T update (Long id, T t);
    T delete(Long id);
    List<T> findAll();
}
