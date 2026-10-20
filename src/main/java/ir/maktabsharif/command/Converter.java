package ir.maktabsharif.command;

import ir.maktabsharif.dao.CommentDao;
import ir.maktabsharif.dao.PostDao;
import ir.maktabsharif.dao.UserDao;
import ir.maktabsharif.dto.*;

public class Converter {
    public static PostDao PostReqDtoToPostDao(PostReqDto postReqDto) {
        return PostDao
                .builder()
                .title(postReqDto.title())
                .content(postReqDto.content())
                .build();
    }

    public static PostRespDto PostDaoToPostRespDto(PostDao postDao) {
        return new PostRespDto(postDao.getId(),
                postDao.getTitle(),
                postDao.getContent(),
                postDao.getAuthor().getId(),
                postDao.getAuthor().getUsername(),
                postDao.getCreatedAt(),
                postDao.getUpdatedAt());
    }

    public static UserDao UserReqDtoToUserDao(UserReqDto userReqDto) {
        return UserDao
                .builder()
                .username(userReqDto.username())
                .email(userReqDto.email())
                .password(userReqDto.password())
                .build();
    }

    public static UserRespDto UserDaoToUserRespDto(UserDao userDao) {
        return  new UserRespDto(userDao.getId(), userDao.getUsername(), userDao.getEmail());
    }

    public static CommentRespDto CommentDaoToCommentRespDto(CommentDao comment) {
        return new CommentRespDto(
                comment.getId(),
                comment.getContent(),
                comment.getAuthor().getId(),
                comment.getAuthor().getUsername(),
                comment.getPost().getId(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }

    public static CommentDao CommentReqDtoToCommentDao(CommentReqDto commentReqDto) {
        return CommentDao
                .builder()
                .content(commentReqDto.content())
                .build();
    }

}
