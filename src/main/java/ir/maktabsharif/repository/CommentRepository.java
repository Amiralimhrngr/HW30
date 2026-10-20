package ir.maktabsharif.repository;

import ir.maktabsharif.dao.CommentDao;
import ir.maktabsharif.repository.base.BaseRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends BaseRepository<CommentDao> {
    List<CommentDao> findByPostId(Long postId);
    Optional<CommentDao> findById(Long id);
}
