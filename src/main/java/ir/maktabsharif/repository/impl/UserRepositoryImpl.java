package ir.maktabsharif.repository.impl;

import ir.maktabsharif.dao.UserDao;
import ir.maktabsharif.repository.UserRepository;
import ir.maktabsharif.repository.base.BaseRepositoryImpl;
import ir.maktabsharif.util.HibernateUtil;

import java.util.Optional;

public class UserRepositoryImpl extends BaseRepositoryImpl<UserDao> implements UserRepository {
    public UserRepositoryImpl() {
        super(UserDao.class);
    }

    @Override
    public void settingAttributes(UserDao upgradingEntity, UserDao newEntity) {
        if (Optional.ofNullable(newEntity.getUsername()).isPresent()) {
            upgradingEntity.setUsername(newEntity.getUsername());
        }
        if (Optional.ofNullable(newEntity.getEmail()).isPresent()) {
            upgradingEntity.setEmail(newEntity.getEmail());
        }
        if (Optional.ofNullable(newEntity.getPassword()).isPresent()) {
            upgradingEntity.setPassword(newEntity.getPassword());
        }
    }

    @Override
    public UserDao findByUsername(String username) {
        return HibernateUtil.read(em ->
                em.createQuery(
                        "select u from UserDao u where lower(u.username) = lower(:username)", UserDao.class)
                        .setParameter("username", username)
                        .getSingleResultOrNull());
    }

    @Override
    public UserDao findByEmail(String email) {
        return HibernateUtil.read(em ->
                em.createQuery(
                        "select u from UserDao u where lower(u.email) = lower(:email)", UserDao.class)
                .setParameter("email", email)
                .getSingleResultOrNull());
    }
}
