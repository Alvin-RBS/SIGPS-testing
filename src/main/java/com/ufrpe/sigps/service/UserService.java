package com.ufrpe.sigps.service;

import com.ufrpe.sigps.dto.UserDto;
import com.ufrpe.sigps.dto.InventorDto;
import com.ufrpe.sigps.dto.AdministratorDto;
import com.ufrpe.sigps.exception.ResourceNotFoundException;
import com.ufrpe.sigps.model.User;
import com.ufrpe.sigps.model.Inventor;
import com.ufrpe.sigps.model.Administrator;
import com.ufrpe.sigps.model.Role;
import com.ufrpe.sigps.repository.UserRepository;
import com.ufrpe.sigps.repository.InventorRepository;
import com.ufrpe.sigps.repository.AdministratorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.ufrpe.sigps.dto.RegisterRequest;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final InventorRepository inventorRepository;
    private final AdministratorRepository administratorRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + id));
        return convertToDto(user);
    }

    public UserDto updateUserDetails(Long id, UserDto userDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + id));

        existingUser.setName(userDto.getName());
        existingUser.setDateBirth(userDto.getDateBirth());
        existingUser.setAddress(userDto.getAddress());
        existingUser.setNationality(userDto.getNationality());
        // Email e CPF geralmente não são alterados

        User updatedUser = userRepository.save(existingUser);
        return convertToDto(updatedUser);
    }

    public void changePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + userId));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private UserDto convertToDto(User user) {
        if (user instanceof Inventor inventor) {
            return InventorDto.builder()
                    .id(inventor.getId())
                    .name(inventor.getName())
                    .dateBirth(inventor.getDateBirth())
                    .email(inventor.getEmail())
                    .cpf(inventor.getCpf())
                    .nationality(inventor.getNationality())
                    .address(inventor.getAddress())
                    .role(inventor.getRole())
                    .course(inventor.getCourse())
                    .department(inventor.getDepartment())
                    .build();
        } else if (user instanceof Administrator admin) {
            return AdministratorDto.builder()
                    .id(admin.getId())
                    .name(admin.getName())
                    .dateBirth(admin.getDateBirth())
                    .email(admin.getEmail())
                    .cpf(admin.getCpf())
                    .nationality(admin.getNationality())
                    .address(admin.getAddress())
                    .role(admin.getRole())
                    .build();
        } else {
            return UserDto.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .dateBirth(user.getDateBirth())
                    .email(user.getEmail())
                    .cpf(user.getCpf())
                    .nationality(user.getNationality())
                    .address(user.getAddress())
                    .role(user.getRole())
                    .build();
        }
    }

    public InventorDto registerInventorByAdmin(RegisterRequest request, String course, String department) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado.");
        }
        if (userRepository.existsByCpf(request.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado.");
        }

        var inventor = Inventor.builder()
                .name(request.getName())
                .dateBirth(request.getDateBirth())
                .email(request.getEmail())
                .cpf(request.getCpf())
                .nationality(request.getNationality())
                .address(request.getAddress())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.INVENTOR)
                .course(course)
                .department(department)
                .build();

        Inventor savedInventor = inventorRepository.save(inventor);
        return (InventorDto) convertToDto(savedInventor);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado com o ID: " + id);
        }
        userRepository.deleteById(id);
    }
}