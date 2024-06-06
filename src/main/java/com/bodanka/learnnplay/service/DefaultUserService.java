package com.bodanka.learnnplay.service;

import com.bodanka.learnnplay.domain.Grade;
import com.bodanka.learnnplay.domain.entity.User;
import com.bodanka.learnnplay.exception.BadRequestException;
import com.bodanka.learnnplay.repository.UserRepository;
import com.bodanka.learnnplay.util.FieldValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationTokenService emailVerificationTokenService;

    @Override
    public User save(User user) {
        validateUser(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id.toString());
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public String deleteById(UUID id) {
        AtomicReference<String> name = new AtomicReference<>();
        findById(id).ifPresent(user -> {
            name.set(StringUtils.capitalize(user.getRole().name().toLowerCase()) + " " + user.getFirstName() + " " + user.getLastName());
            emailVerificationTokenService.deleteByUser(user);
            userRepository.delete(user);
        });
        return name.get() + " were deleted";
    }

    @Override
    public void enable(UUID id) {
        userRepository.enable(id.toString());
    }

    @Override
    public void addStudent(User teacher, User student) {
        teacher.getStudents().add(student);
        userRepository.save(teacher);
    }

    @Override
    public List<User> findByTeacher(UUID teacherId) {
        return userRepository.findByTeacherIdAndEnabledTrue(teacherId.toString());
    }

    @Override
    public void increaseGradeByUserId(String userId, Grade grade) {
        userRepository.increaseGradeByUserId(userId, grade);
    }

    private void validateUser(User user) {
        validateRequiredFields(user);

        if (!FieldValidator.validateEmail(user.getEmail())) {
            throw new BadRequestException("Invalid email: " + user.getEmail());
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BadRequestException("Email already in use: " + user.getEmail());
        }

        if (!FieldValidator.validatePassword(user.getPassword(), false)) {
            throw new BadRequestException("Invalid password: " + user.getPassword());
        }
    }

    private void validateRequiredFields(User user) {
        if (!FieldValidator.validateStringHasText(user.getFirstName())) {
            throw new BadRequestException("First name is required");
        }

        if (!FieldValidator.validateStringHasText(user.getLastName())) {
            throw new BadRequestException("Last name is required");
        }

        if (!FieldValidator.validateStringHasText(user.getEmail())) {
            throw new BadRequestException("Email is required");
        }

        if (!FieldValidator.validateStringHasText(user.getPassword())) {
            throw new BadRequestException("Password is required");
        }

        if (!FieldValidator.validateNonNull(user.getRole())) {
            throw new BadRequestException("Role is required");
        }
    }
}
