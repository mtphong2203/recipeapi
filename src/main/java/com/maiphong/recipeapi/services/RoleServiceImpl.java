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
import com.maiphong.recipeapi.repositories.RoleRepository;

import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<RoleDTO> findAll() {
        var roles = roleRepository.findAll();

        var rolesDTOs = roles.stream().map(c -> {
            var roleDTO = new RoleDTO();
            roleDTO.setId(c.getId());
            roleDTO.setName(c.getName());
            roleDTO.setDescription(c.getDescription());
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

        var roleDTO = new RoleDTO();
        roleDTO.setId(role.getId());
        roleDTO.setName(role.getName());
        roleDTO.setDescription(role.getDescription());

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

        var role = new Role();
        role.setName(roleCreateDTO.getName());
        role.setDescription(roleCreateDTO.getDescription());

        roleRepository.save(role);

        var updateRoleDTO = new RoleDTO();
        updateRoleDTO.setId(role.getId());
        updateRoleDTO.setName(role.getName());
        updateRoleDTO.setDescription(role.getDescription());

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
        role.setName(roleDTO.getName());
        role.setDescription(roleDTO.getDescription());

        // Save role => update
        role = roleRepository.save(role);

        // Convert Role to RoleDTO
        var updatedRoleDTO = new RoleDTO();
        updatedRoleDTO.setId(role.getId());
        updatedRoleDTO.setName(role.getName());
        updatedRoleDTO.setDescription(role.getDescription());

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
            var roleDTO = new RoleDTO();
            roleDTO.setId(role.getId());
            roleDTO.setName(role.getName());
            roleDTO.setDescription(role.getDescription());
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
            var roleDTO = new RoleDTO();
            roleDTO.setId(role.getId());
            roleDTO.setName(role.getName());
            roleDTO.setDescription(role.getDescription());
            return roleDTO;
        });

        return roleDTOs;
    }

}
