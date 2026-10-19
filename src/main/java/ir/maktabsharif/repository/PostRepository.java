package ir.maktabsharif.repository;

import ir.maktabsharif.dao.PostDao;
import ir.maktabsharif.repository.base.BaseRepository;

import java.util.Optional;

public interface PostRepository extends BaseRepository<PostDao> {
    Optional<PostDao> findById(Long Id);
}
