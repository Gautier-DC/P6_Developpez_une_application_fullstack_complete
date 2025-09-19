package com.openclassrooms.mddapi.service.impl;

import com.openclassrooms.mddapi.dto.request.CreateThemeRequest;
import com.openclassrooms.mddapi.dto.response.ThemeResponse;
import com.openclassrooms.mddapi.exception.ThemeAlreadyExistsException;
import com.openclassrooms.mddapi.exception.ThemeNotFoundException;
import com.openclassrooms.mddapi.model.Theme;
import com.openclassrooms.mddapi.repository.ThemeRepository;
import com.openclassrooms.mddapi.service.ThemeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ThemeServiceImpl implements ThemeService {

    @Autowired
    private ThemeRepository themeRepository;

    @Override
    public ThemeResponse createTheme(CreateThemeRequest request) {
        log.info("Creating new theme with name: {}", request.getName());

        if (themeRepository.existsByName(request.getName())) {
            throw ThemeAlreadyExistsException.withName(request.getName());
        }

        Theme theme = new Theme(request.getName(), request.getDescription());
        Theme savedTheme = themeRepository.save(theme);

        log.info("Theme created successfully with ID: {}", savedTheme.getId());
        return convertToResponse(savedTheme);
    }

    @Override
    public List<ThemeResponse> getAllThemes() {
        log.info("Fetching all themes");
        return themeRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ThemeResponse getThemeById(Long id) {
        log.info("Fetching theme with ID: {}", id);
        Theme theme = themeRepository.findById(id)
                .orElseThrow(() -> new ThemeNotFoundException(id));
        return convertToResponse(theme);
    }

    @Override
    public ThemeResponse updateTheme(Long id, CreateThemeRequest request) {
        log.info("Updating theme with ID: {}", id);

        Theme theme = themeRepository.findById(id)
                .orElseThrow(() -> new ThemeNotFoundException(id));

        if (!theme.getName().equals(request.getName()) && themeRepository.existsByName(request.getName())) {
            throw ThemeAlreadyExistsException.withName(request.getName());
        }

        theme.setName(request.getName());
        theme.setDescription(request.getDescription());

        Theme updatedTheme = themeRepository.save(theme);
        log.info("Theme updated successfully with ID: {}", updatedTheme.getId());
        return convertToResponse(updatedTheme);
    }

    @Override
    public void deleteTheme(Long id) {
        log.info("Deleting theme with ID: {}", id);

        if (!themeRepository.existsById(id)) {
            throw new ThemeNotFoundException(id);
        }

        themeRepository.deleteById(id);
        log.info("Theme deleted successfully with ID: {}", id);
    }

    @Override
    public boolean existsByName(String name) {
        return themeRepository.existsByName(name);
    }

    private ThemeResponse convertToResponse(Theme theme) {
        return new ThemeResponse(
                theme.getId(),
                theme.getName(),
                theme.getDescription(),
                theme.getCreatedAt(),
                theme.getUpdatedAt()
        );
    }
}