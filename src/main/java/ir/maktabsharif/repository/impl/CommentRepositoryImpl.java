package ir.maktabsharif.repository.impl;

import ir.maktabsharif.dao.CommentDao;
import ir.maktabsharif.repository.CommentRepository;
import ir.maktabsharif.repository.base.BaseRepositoryImpl;
import ir.maktabsharif.util.HibernateUtil;

import java.util.List;
import java.util.Optional;

public class CommentRepositoryImpl extends BaseRepositoryImpl<CommentDao> implements CommentRepository {

    public CommentRepositoryImpl() {
        super(CommentDao.class);
    }

    @Override
    public void settingAttributes(CommentDao oldComment, CommentDao newComment) {
        if (newComment.getContent() != null) {
            oldComment.setContent(newComment.getContent());
        }
    }

    @Override
    public List<CommentDao> findByPostId(Long postId) {
        return HibernateUtil.read(em -> em.createQuery(
                        "select c from CommentDao c join fetch c.author join fetch c.post " +
                                "where c.post.id = :postId order by c.createdAt asc", CommentDao.class)
                .setParameter("postId", postId)
                .getResultList());
    }

    @Override
    public Optional<CommentDao> findById(Long id) {
        return HibernateUtil.read(em -> em.createQuery(
                        "select c from CommentDao c join fetch c.author join fetch c.post " +
                                "where c.id = :id", CommentDao.class)
                .setParameter("id", id)
                .getResultList()
                .stream()
                .findFirst());
    }
}
