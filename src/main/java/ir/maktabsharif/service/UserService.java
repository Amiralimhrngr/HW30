package ir.maktabsharif.service;


import ir.maktabsharif.command.Converter;
import ir.maktabsharif.dao.UserDao;
import ir.maktabsharif.dto.UserReqDto;
import ir.maktabsharif.dto.UserRespDto;
import ir.maktabsharif.exception.AppException;
import ir.maktabsharif.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;

import java.util.regex.Pattern;

public class UserService {
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9_.-]{3,30}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserRespDto register(UserReqDto userReqDto) {
        if (userReqDto == null) {
            throw new AppException(HttpServletResponse.SC_BAD_REQUEST, "Registration data is required.");
        }
        UserDao userDao = Converter.UserReqDtoToUserDao(userReqDto);

        if (userDao.getUsername() == null || !USERNAME_PATTERN.matcher(userDao.getUsername()).matches()) {
            throw new AppException(HttpServletResponse.SC_BAD_REQUEST, "Username must be 3-30 characters and contain only letters, digits, dot, underscore or hyphen.");
        }
        if (userDao.getEmail() == null || userDao.getEmail().length() > 254 || !EMAIL_PATTERN.matcher(userDao.getEmail()).matches()) {
            throw new AppException(HttpServletResponse.SC_BAD_REQUEST, "A valid email address is required.");
        }
        if (userDao.getPassword() == null || userDao.getPassword().length() < 8 || userDao.getPassword().length() > 128) {
            throw new AppException(HttpServletResponse.SC_BAD_REQUEST, "Password must contain between 8 and 128 characters.");
        }
        if (userRepository.findByUsername(userDao.getUsername()) != null) {
            throw new AppException(HttpServletResponse.SC_CONFLICT, "Username is already registered.");
        }
        if (userRepository.findByEmail(userDao.getEmail()) != null) {
            throw new AppException(HttpServletResponse.SC_CONFLICT, "Email is already registered.");
        }
        return Converter.UserDaoToUserRespDto(userRepository.create(userDao));
    }

    public UserRespDto login(UserReqDto userReqDto) {
        if (userReqDto == null || userReqDto.username() == null || userReqDto.password() == null) {
            throw new AppException(HttpServletResponse.SC_BAD_REQUEST, "Username and password are required.");
        }
        UserDao user = userRepository.findByUsername(userReqDto.username());

        if (user == null || !userReqDto.password().equals(user.getPassword())) {
            throw new AppException(HttpServletResponse.SC_UNAUTHORIZED, "Invalid username or password.");
        }
        return Converter.UserDaoToUserRespDto(user);
    }

}
