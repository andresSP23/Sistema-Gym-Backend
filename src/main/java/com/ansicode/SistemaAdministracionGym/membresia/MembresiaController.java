package com.ansicode.SistemaAdministracionGym.membresia;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("membresias")
@Tag(name = "Membresia")
@RequiredArgsConstructor
public class MembresiaController {


    private final MembresiaService service;

    @PostMapping("/create")
    public ResponseEntity<MembresiaResponse> create(
            @Valid @RequestBody MembresiaRequest request
    ) {
        return ResponseEntity.ok(service.create(request));
    }

    @GetMapping("findAll")
    public ResponseEntity<PageResponse<MembresiaResponse>> findAll(
            @RequestParam(name = "page", defaultValue = "0" ,required = false) int page,
            @RequestParam(name = "size", defaultValue = "10" ,required = false) int size,
            Pageable pageable
    ) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<MembresiaResponse> findById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<MembresiaResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody MembresiaRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
