package ru.practicum.main.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.common.GlobalConstant;
import ru.practicum.main.dto.CompilationDto;
import ru.practicum.main.service.CompilationService;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
public class PublicCompilationController {

    private final CompilationService compilationService;

    @GetMapping
    public ResponseEntity<List<CompilationDto>> getCompilations(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = GlobalConstant.DEFAULT_FROM) Integer from,
            @RequestParam(defaultValue = GlobalConstant.DEFAULT_SIZE) Integer size) {

        List<CompilationDto> compilations = compilationService.getCompilations(pinned, from, size);
        return ResponseEntity.ok(compilations);
    }

    @GetMapping("/{compId}")
    public ResponseEntity<CompilationDto> getCompilation(@PathVariable Long compId) {
        CompilationDto compilation = compilationService.getCompilation(compId);
        return ResponseEntity.ok(compilation);
    }
}
