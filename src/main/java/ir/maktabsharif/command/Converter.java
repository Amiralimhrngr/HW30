package ir.maktabsharif.command;

import ir.maktabsharif.dao.PostDao;
import ir.maktabsharif.dao.UserDao;
import ir.maktabsharif.dto.PostReqDto;
import ir.maktabsharif.dto.PostRespDto;
import ir.maktabsharif.dto.UserReqDto;
import ir.maktabsharif.dto.UserRespDto;

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

}
