package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.request.CreateThemeRequest;
import com.openclassrooms.mddapi.dto.response.ThemeResponse;

import java.util.List;

public interface ThemeService {

    ThemeResponse createTheme(CreateThemeRequest request);

    List<ThemeResponse> getAllThemes();

    ThemeResponse getThemeById(Long id);

    ThemeResponse updateTheme(Long id, CreateThemeRequest request);

    void deleteTheme(Long id);

    boolean existsByName(String name);
}