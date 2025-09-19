package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.request.CreateThemeRequest;
import com.openclassrooms.mddapi.dto.response.ThemeResponse;
import com.openclassrooms.mddapi.service.ThemeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/themes")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Themes", description = "Theme management APIs")
public class ThemeController {

    @Autowired
    private ThemeService themeService;

    @PostMapping
    @Operation(summary = "Create a new theme", description = "Create a new theme for articles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Theme created successfully",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ThemeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data or theme already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ThemeResponse> createTheme(@Valid @RequestBody CreateThemeRequest request) {
        log.info("Creating theme with name: {}", request.getName());

        ThemeResponse response = themeService.createTheme(request);
        log.info("Theme created successfully with ID: {}", response.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all themes", description = "Retrieve all available themes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Themes retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<ThemeResponse>> getAllThemes() {
        log.info("Fetching all themes");

        List<ThemeResponse> themes = themeService.getAllThemes();
        log.info("Retrieved {} themes", themes.size());

        return ResponseEntity.ok(themes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get theme by ID", description = "Retrieve a specific theme by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Theme retrieved successfully",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ThemeResponse.class))),
            @ApiResponse(responseCode = "404", description = "Theme not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ThemeResponse> getThemeById(@PathVariable Long id) {
        log.info("Fetching theme with ID: {}", id);

        ThemeResponse theme = themeService.getThemeById(id);
        return ResponseEntity.ok(theme);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update theme", description = "Update an existing theme")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Theme updated successfully",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ThemeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Theme not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ThemeResponse> updateTheme(@PathVariable Long id,
                                                   @Valid @RequestBody CreateThemeRequest request) {
        log.info("Updating theme with ID: {}", id);

        ThemeResponse response = themeService.updateTheme(id, request);
        log.info("Theme updated successfully with ID: {}", response.getId());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete theme", description = "Delete an existing theme")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Theme deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Theme not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteTheme(@PathVariable Long id) {
        log.info("Deleting theme with ID: {}", id);

        themeService.deleteTheme(id);
        log.info("Theme deleted successfully with ID: {}", id);

        return ResponseEntity.noContent().build();
    }
}