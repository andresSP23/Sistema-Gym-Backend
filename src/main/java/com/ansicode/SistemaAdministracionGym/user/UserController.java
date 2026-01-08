package com.ansicode.SistemaAdministracionGym.user;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("users")
@Tag(name = "User")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@RequestBody @Valid UserRequest request , Authentication connectedUser) {
        return userService.create(request , connectedUser);
    }

    @GetMapping("/findAll")
    public PageResponse<UserResponse> findAll(Pageable pageable) {
        return userService.findAll(pageable);
    }

    @GetMapping("/findById/{id}")
    public UserResponse findById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PutMapping("/update/{id}")
    public UserResponse update(
            @PathVariable Long id,
            @RequestBody @Valid UserRequest request
    ) {
        return userService.update(id, request);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}
