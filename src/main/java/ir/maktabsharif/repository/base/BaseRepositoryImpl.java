package ir.maktabsharif.repository.base;

import ir.maktabsharif.util.HibernateUtil;
import java.util.List;
import java.util.Optional;

public abstract class BaseRepositoryImpl<T> implements BaseRepository<T> {

    private final Class<T> entityClass;

    public BaseRepositoryImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public T create(T t) {
        return HibernateUtil.inTxReturn(em -> {
            em.persist(t);
            return t;
        });
    }

    @Override
    public Optional<T> findById(Long id) {
        return Optional.ofNullable(HibernateUtil.read(em -> em.find(entityClass, id)));
    }

    @Override
    public T update(Long id, T newEntity) {
        return HibernateUtil.inTxReturn(
                em -> {
                    T upgradingEntity = em.find(entityClass, id);
                    settingAttributes(upgradingEntity, newEntity);
                    return upgradingEntity;
                });
    }

    @Override
    public T delete(Long id) {
        return HibernateUtil.inTxReturn(
                em -> {
                    T t = em.find(entityClass, id);
                    if (t == null) {
                        return null;
                    }
                    em.remove(t);
                    return t;
                }
        );
    }

    @Override
    public List<T> findAll() {
       return HibernateUtil.read(
               em ->
                       em.createQuery("FROM " + entityClass.getSimpleName(), entityClass).getResultList());
    }

    public abstract void settingAttributes(T upgradingEntity, T newEntity);
}
