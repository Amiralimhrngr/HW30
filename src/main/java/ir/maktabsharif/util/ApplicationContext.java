package ir.maktabsharif.util;

import ir.maktabsharif.repository.PostRepository;
import ir.maktabsharif.repository.UserRepository;
import ir.maktabsharif.repository.impl.PostRepositoryImpl;
import ir.maktabsharif.repository.impl.UserRepositoryImpl;
import ir.maktabsharif.service.UserService;
import ir.maktabsharif.service.PostService;

public final class ApplicationContext {
    private static final UserRepository USER_REPOSITORY;
    private static final UserService USER_SERVICE;
    private static final PostRepository POST_REPOSITORY;
    private static final PostService POST_SERVICE;

    static {
        USER_REPOSITORY = new UserRepositoryImpl();
        USER_SERVICE = new UserService(USER_REPOSITORY);
        POST_REPOSITORY = new PostRepositoryImpl();
        POST_SERVICE = new PostService(POST_REPOSITORY, USER_REPOSITORY);
    }

    public static UserService getUserService() {
        return USER_SERVICE;
    }

    public static UserRepository getUserRepository() {
        return USER_REPOSITORY;
    }

    public static PostRepository getPostRepository() {
        return POST_REPOSITORY;
    }

    public static PostService getPostService() {
        return POST_SERVICE;
    }
}
