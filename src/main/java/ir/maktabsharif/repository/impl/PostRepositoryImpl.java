package ir.maktabsharif.repository.impl;

import ir.maktabsharif.dao.PostDao;
import ir.maktabsharif.repository.PostRepository;
import ir.maktabsharif.repository.base.BaseRepositoryImpl;
import ir.maktabsharif.util.HibernateUtil;

import java.util.List;
import java.util.Optional;

public class PostRepositoryImpl extends BaseRepositoryImpl<PostDao> implements PostRepository {

    public PostRepositoryImpl() {
        super(PostDao.class);
    }

    @Override
    public void settingAttributes(PostDao upgradingEntity, PostDao newEntity) {
        if (Optional.ofNullable(newEntity.getTitle()).isPresent()) {
            upgradingEntity.setTitle(newEntity.getTitle());
        }
        if (Optional.ofNullable(newEntity.getContent()).isPresent()) {
            upgradingEntity.setContent(newEntity.getContent());
        }
    }

    @Override
    public Optional<PostDao> findById(Long id) {
        return Optional.ofNullable(HibernateUtil.read(em ->
                em.createQuery(
                                "select p from PostDao p join fetch p.author where p.id = :id",
                                PostDao.class)
                        .setParameter("id", id)
                        .getSingleResult()
        ));
    }

    @Override
    public List<PostDao> findAll() {
        return HibernateUtil.read(em ->
                em.createQuery(
                                "select p from PostDao p join fetch p.author order by p.createdAt desc",
                                PostDao.class)
                        .getResultList()
        );
    }
}
