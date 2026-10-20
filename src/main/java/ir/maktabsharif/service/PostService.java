package ir.maktabsharif.service;


import ir.maktabsharif.command.Converter;
import ir.maktabsharif.dao.PostDao;
import ir.maktabsharif.dao.UserDao;
import ir.maktabsharif.dto.PostReqDto;
import ir.maktabsharif.dto.PostRespDto;
import ir.maktabsharif.exception.AppException;
import ir.maktabsharif.repository.PostRepository;
import ir.maktabsharif.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Optional;

import static ir.maktabsharif.command.Converter.PostDaoToPostRespDto;
import static ir.maktabsharif.command.Converter.PostReqDtoToPostDao;

public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public List<PostRespDto> findAll() {
        return postRepository.findAll()
                .stream()
                .map(Converter::PostDaoToPostRespDto)
                .toList();
    }

    public PostRespDto findById(Long id) {
        validateId(id);
        Optional<PostDao> postDao = postRepository.findById(id);
            if (postDao.isEmpty()) {
                throw new AppException(HttpServletResponse.SC_NOT_FOUND, "Post not found.");
            }
            return PostDaoToPostRespDto(postDao.get());

    }

    public PostRespDto create(Long authorId, PostReqDto postReqDto) {
        validatePost(postReqDto);
        Optional<UserDao> author = userRepository.findById(authorId);
        if (author.isEmpty()) {
            throw new AppException(HttpServletResponse.SC_UNAUTHORIZED, "Please log in again.");
        }
        PostDao postDao = PostReqDtoToPostDao(postReqDto);
        postDao.setAuthor(author.get());
        return PostDaoToPostRespDto(postRepository.create(postDao));
    }

    public PostRespDto update(Long currentUserId, Long postId, PostReqDto postReqDto) {
        validateId(postId);
        validatePost(postReqDto);

        Optional<PostDao> postDaoOptional = postRepository.findById(postId);
        if (postDaoOptional.isEmpty()) {
            throw new AppException(HttpServletResponse.SC_NOT_FOUND, "Post not found.");
        }
        PostDao postDao = postDaoOptional.get();
        if (!postDao.getAuthor().getId().equals(currentUserId)) {
            throw new AppException(HttpServletResponse.SC_FORBIDDEN, "You can only edit your own posts.");
        }
        return PostDaoToPostRespDto(postRepository.update(postId, PostReqDtoToPostDao(postReqDto)));
    }

    public void delete(Long currentUserId, Long postId) {
        validateId(postId);

        Optional<PostDao> postDaoOptional = postRepository.findById(postId);
        if (postDaoOptional.isEmpty()) {
            throw new AppException(HttpServletResponse.SC_NOT_FOUND, "Post not found.");
        }
        PostDao postDao = postDaoOptional.get();
        if (!postDao.getAuthor().getId().equals(currentUserId)) {
            throw new AppException(HttpServletResponse.SC_FORBIDDEN, "You can only delete your own posts.");
        }
        postRepository.delete(postId);
    }

    private void validatePost(PostReqDto postReqDto) {
        if (Optional.ofNullable(postReqDto).isEmpty()) {
            throw new AppException(HttpServletResponse.SC_BAD_REQUEST, "Post data is required.");
        }
        if (Optional.ofNullable(postReqDto.title()).isEmpty() || postReqDto.title().isEmpty() || postReqDto.title().isBlank() || postReqDto.title().length() > 160) {
            throw new AppException(HttpServletResponse.SC_BAD_REQUEST, "Title is required and must be at most 160 characters.");
        }
        if (postReqDto.content().isEmpty() || postReqDto.content().isBlank() || postReqDto.content().length() > 20000) {
            throw new AppException(400, "Content is required and must be at most 20000 characters.");
        }
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new AppException(400, "A valid post ID is required.");
        }
    }
}
