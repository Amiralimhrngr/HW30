package ir.maktabsharif.service;

import ir.maktabsharif.command.Converter;
import ir.maktabsharif.dao.CommentDao;
import ir.maktabsharif.dao.PostDao;
import ir.maktabsharif.dao.UserDao;
import ir.maktabsharif.dto.CommentReqDto;
import ir.maktabsharif.dto.CommentRespDto;
import ir.maktabsharif.exception.AppException;
import ir.maktabsharif.repository.CommentRepository;
import ir.maktabsharif.repository.PostRepository;
import ir.maktabsharif.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Optional;

public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public List<CommentRespDto> findByPostId(Long postId) {
        validateId(postId, "post");
        return commentRepository.findByPostId(postId)
                .stream()
                .map(Converter::CommentDaoToCommentRespDto)
                .toList();
    }

    public CommentRespDto create(Long userId, Long postId, CommentReqDto commentReqDto) {
        validateId(postId, "post");
        validateContent(commentReqDto);

        UserDao author = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(HttpServletResponse.SC_UNAUTHORIZED, "Please log in again."));
        PostDao post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(HttpServletResponse.SC_NOT_FOUND, "Post not found."));

        CommentDao comment = Converter.CommentReqDtoToCommentDao(commentReqDto);
        comment.setAuthor(author);
        comment.setPost(post);

        return Converter.CommentDaoToCommentRespDto(commentRepository.create(comment));
    }

    public CommentRespDto update(Long userId, Long commentId, CommentReqDto commentReqDto) {
        validateId(commentId, "comment");
        validateContent(commentReqDto);

        CommentDao oldComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(HttpServletResponse.SC_NOT_FOUND, "Comment not found."));

        if (!oldComment.getAuthor().getId().equals(userId)) {
            throw new AppException(HttpServletResponse.SC_FORBIDDEN, "You can only edit your own comments.");
        }

        commentRepository.update(commentId, Converter.CommentReqDtoToCommentDao(commentReqDto));

        CommentDao updatedComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(HttpServletResponse.SC_NOT_FOUND, "Comment not found."));
        return Converter.CommentDaoToCommentRespDto(updatedComment);
    }

    public void delete(Long userId, Long commentId) {
        validateId(commentId, "comment");

        CommentDao comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(HttpServletResponse.SC_NOT_FOUND, "Comment not found."));

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new AppException(HttpServletResponse.SC_FORBIDDEN, "You can only delete your own comments.");
        }

        commentRepository.delete(commentId);
    }

    private void validateContent(CommentReqDto commentReqDto) {
        if (Optional.ofNullable(commentReqDto).isEmpty() ||
                Optional.ofNullable(commentReqDto.content()).isEmpty() ||
                commentReqDto.content().isBlank()
                || commentReqDto.content().length() > 5000) {
            throw new AppException(HttpServletResponse.SC_BAD_REQUEST,
                    "Comment must not be empty and must be at most 5000 characters.");
        }
    }

    private void validateId(Long id, String type) {
        if (Optional.ofNullable(id).isEmpty() || id <= 0) {
            throw new AppException(HttpServletResponse.SC_BAD_REQUEST, "A valid " + type + " ID is required.");
        }
    }
}
