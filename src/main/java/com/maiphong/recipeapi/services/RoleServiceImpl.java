package com.maiphong.recipeapi.services;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.maiphong.recipeapi.dtos.role.RoleCreateDTO;
import com.maiphong.recipeapi.dtos.role.RoleDTO;
import com.maiphong.recipeapi.entities.Role;
import com.maiphong.recipeapi.map.role.RoleMapper;
import com.maiphong.recipeapi.repositories.RoleRepository;

import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public RoleServiceImpl(RoleRepository roleRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    @Override
    public List<RoleDTO> findAll() {
        var roles = roleRepository.findAll();

        var rolesDTOs = roles.stream().map(c -> {
            var roleDTO = roleMapper.toRoleDTO(c);
            return roleDTO;
        }).toList();

        return rolesDTOs;
    }

    @Override
    public RoleDTO findById(UUID id) {
        var role = roleRepository.findById(id).orElse(null);

        if (role == null) {
            return null;
        }
        var roleDTO = roleMapper.toRoleDTO(role);
        return roleDTO;
    }

    @Override
    public RoleDTO create(RoleCreateDTO roleCreateDTO) {
        if (roleCreateDTO == null) {
            throw new IllegalArgumentException("RoleDTO is required");
        }

        var exist = roleRepository.findByName(roleCreateDTO.getName());
        if (exist != null) {
            throw new IllegalArgumentException("RoleDTO is exist!");
        }

        var role = roleMapper.toRole(roleCreateDTO);

        role = roleRepository.save(role);

        var updateRoleDTO = roleMapper.toRoleDTO(role);
        return updateRoleDTO;
    }

    @Override
    public RoleDTO update(UUID id, RoleDTO roleDTO) {
        if (roleDTO == null) {
            throw new IllegalArgumentException("RoleDTO is required");
        }

        // Checl if role name is existed
        var existedRole = roleRepository.findByName(roleDTO.getName());
        if (existedRole != null && !existedRole.getId().equals(id)) {
            throw new IllegalArgumentException("Role name is existed");
        }

        // Find role by id - Managed
        var role = roleRepository.findById(id).orElse(null);

        if (role == null) {
            throw new IllegalArgumentException("Role not found");
        }

        // Update role
        roleMapper.toRole(roleDTO);

        // Save role => update
        role = roleRepository.save(role);

        // Convert Role to RoleDTO
        var updatedRoleDTO = roleMapper.toRoleDTO(role);
        return updatedRoleDTO;
    }

    @Override
    public boolean delete(UUID id) {
        var role = roleRepository.findById(id).orElse(null);

        if (role == null) {
            throw new IllegalArgumentException("Role not found");
        }
        roleRepository.delete(role);

        return !roleRepository.existsById(id);
    }

    @Override
    public List<RoleDTO> search(String keyword) {
        // Find role by keyword
        Specification<Role> specification = (root, query, criteriaBuilder) -> {
            // Neu keyword null thi tra ve null
            if (keyword == null) {
                return null;
            }

            // Neu keyword khong null
            // WHERE LOWER(name) LIKE %keyword%
            Predicate namePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                    "%" + keyword.toLowerCase() + "%");

            // WHERE LOWER(description) LIKE %keyword%
            Predicate desPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),
                    "%" + keyword.toLowerCase() + "%");

            // WHERE LOWER(name) LIKE %keyword% OR LOWER(description) LIKE %keyword%
            return criteriaBuilder.or(namePredicate, desPredicate);
        };

        var roles = roleRepository.findAll(specification);

        // Covert List<Role> to List<RoleDTO>
        var roleDTOs = roles.stream().map(role -> {
            var roleDTO = roleMapper.toRoleDTO(role);
            return roleDTO;
        }).toList();

        return roleDTOs;

    }

    @Override
    public Page<RoleDTO> search(String keyword, Pageable pageable) {
        Specification<Role> specification = (root, query, criteriaBuilder) -> {
            if (keyword == null) {
                return null;
            }

            // WHERE name LIKE %keyword% OR description LIKE %keyword%
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),
                            "%" + keyword.toLowerCase() + "%"));
        };

        Page<Role> roles = roleRepository.findAll(specification, pageable);

        Page<RoleDTO> roleDTOs = roles.map(role -> {
            var roleDTO = roleMapper.toRoleDTO(role);
            return roleDTO;
        });

        return roleDTOs;
    }

}
