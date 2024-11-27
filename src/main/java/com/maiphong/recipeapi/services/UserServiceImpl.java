package com.maiphong.recipeapi.services;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.criteria.Predicate;

import com.maiphong.recipeapi.entities.User;
import com.maiphong.recipeapi.map.user.UserMapper;
import com.maiphong.recipeapi.repositories.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maiphong.recipeapi.dtos.user.UserCreateDTO;
import com.maiphong.recipeapi.dtos.user.UserDTO;
import com.maiphong.recipeapi.dtos.user.UserEditDTO;

import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    // Inject UserRepository via constructor
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    public List<UserDTO> findAll() {
        var users = userRepository.findAll();

        var userDTOs = users.stream().map(user -> {
            var userDTO = userMapper.toUserDTO(user);
            return userDTO;
        }).toList();

        return userDTOs;
    }

    @Override
    public List<UserDTO> search(String keyword) {
        // Find user by keyword
        Specification<User> specification = (root, query, criteriaBuilder) -> {
            // Neu keyword null thi tra ve null
            if (keyword == null) {
                return null;
            }

            // Neu keyword khong null
            // WHERE LOWER(firstName) LIKE %keyword%
            Predicate firstNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")),
                    "%" + keyword.toLowerCase() + "%");

            // WHERE LOWER(lastName) LIKE %keyword%
            Predicate lastNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")),
                    "%" + keyword.toLowerCase() + "%");

            // WHERE LOWER(username) LIKE %keyword%
            Predicate useramePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("username")),
                    "%" + keyword.toLowerCase() + "%");

            // WHERE LOWER(email) LIKE %keyword%
            Predicate emailPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("email")),
                    "%" + keyword.toLowerCase() + "%");

            // WHERE LOWER(name) LIKE %keyword% OR LOWER(description) LIKE %keyword%
            return criteriaBuilder.or(firstNamePredicate, lastNamePredicate, useramePredicate, emailPredicate);
        };

        var users = userRepository.findAll(specification);

        // Covert List<User> to List<UserDTO>
        var userDTOs = users.stream().map(user -> {
            var userDTO = userMapper.toUserDTO(user);
            return userDTO;
        }).toList();

        return userDTOs;
    }

    @Override
    public Page<UserDTO> search(String keyword, Pageable pageable) {
        // Find user by keyword
        Specification<User> specification = (root, query, criteriaBuilder) -> {
            // Neu keyword null thi tra ve null
            if (keyword == null) {
                return null;
            }

            // Neu keyword khong null
            // WHERE LOWER(firstName) LIKE %keyword%
            Predicate firstNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")),
                    "%" + keyword.toLowerCase() + "%");

            // WHERE LOWER(lastName) LIKE %keyword%
            Predicate lastNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")),
                    "%" + keyword.toLowerCase() + "%");

            // WHERE LOWER(username) LIKE %keyword%
            Predicate useramePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("username")),
                    "%" + keyword.toLowerCase() + "%");

            // WHERE LOWER(email) LIKE %keyword%
            Predicate emailPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("email")),
                    "%" + keyword.toLowerCase() + "%");

            // WHERE LOWER(name) LIKE %keyword% OR LOWER(description) LIKE %keyword%
            return criteriaBuilder.or(firstNamePredicate, lastNamePredicate, useramePredicate, emailPredicate);
        };

        var users = userRepository.findAll(specification, pageable);

        // Covert Page<User> to Page<UserDTO>
        var userDTOs = users.map(user -> {
            var userDTO = userMapper.toUserDTO(user);
            return userDTO;
        });

        return userDTOs;
    }

    @Override
    public UserDTO findById(UUID id) {
        var user = userRepository.findById(id).orElse(null);

        if (user == null) {
            return null;
        }

        var userDTO = userMapper.toUserDTO(user);

        return userDTO;
    }

    @Override
    public boolean create(UserCreateDTO userCreateDTO) {
        // Kiem tra userDTO null
        if (userCreateDTO == null) {
            throw new IllegalArgumentException("User is required");
        }

        // Check if user name or email is existed
        var existedUser = userRepository.findByUserNameOrEmail(userCreateDTO.getUsername(), userCreateDTO.getEmail());
        if (existedUser != null) {
            throw new IllegalArgumentException("User name or email is existed");
        }

        // Convert UserDTO to User
        var user = userMapper.toUser(userCreateDTO);
        // Hash password with BCrypt: Admin@1234(DTO) => $2a$10$3Q7...(Database
        user.setPassword(passwordEncoder.encode(userCreateDTO.getPassword()));
        // Save user
        user = userRepository.save(user);

        return user != null;
    }

    @Override
    public boolean update(UUID id, UserEditDTO userEditDTO) {
        if (userEditDTO == null) {
            throw new IllegalArgumentException("User is required");
        }

        // Find user by id - Managed
        var user = userRepository.findById(id).orElse(null);

        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        // Cho phep user update username/email nhung chi khi ko co ai trung thong tin do
        // Check if user name is existed
        var existedUser = userRepository.findByUserNameOrEmail(userEditDTO.getUsername(), userEditDTO.getEmail());
        if (existedUser != null && !existedUser.getId().equals(id)) {
            throw new IllegalArgumentException("User name or email is existed");
        }

        // Update user
        userMapper.toUser(userEditDTO);
        user.setPassword(passwordEncoder.encode(userEditDTO.getPassword()));
        // Save user => update
        user = userRepository.save(user);

        return user != null;
    }

    @Override
    public boolean delete(UUID id) {
        // Find user by id - Managed
        var user = userRepository.findById(id).orElse(null);

        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        // Delete user
        userRepository.delete(user);

        // Check if user is deleted
        return !userRepository.existsById(id);
    }
}
