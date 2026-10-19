package ir.maktabsharif.repository;

import ir.maktabsharif.dao.UserDao;
import ir.maktabsharif.repository.base.BaseRepository;

public interface UserRepository extends BaseRepository<UserDao> {
    UserDao findByUsername(String username);
    UserDao findByEmail(String email);
}
