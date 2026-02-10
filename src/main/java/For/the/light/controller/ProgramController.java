package For.the.light.controller;

import For.the.light.dto.ProgramDto;
import For.the.light.dto.ProgramStatusUpdateDto;
import For.the.light.dto.UserDTO;
import For.the.light.service.ProgramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/program")
@RequiredArgsConstructor
@Slf4j
public class ProgramController {

    private final ProgramService programService;

    @PostMapping
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<ProgramDto> createProgram(@RequestBody ProgramDto programDto, Authentication authentication) {
        log.info("Request to create program: {}", programDto.getTitle());

        String email = authentication.getName();
        ProgramDto createdProgram = programService.createProgram(programDto, email);

        return ResponseEntity.ok(createdProgram);
    }

    @PostMapping("/{id}/enroll")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<?> enrollUser(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        log.info("Request to enroll user {} in program id: {}", email, id);

        programService.enrollUser(id, email);

        return ResponseEntity.ok(Map.of("message", "Enrolled successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<List<ProgramDto>> getAllPrograms(Authentication authentication) {
        log.info("Request to get all programs");
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(programService.getAllPrograms(email));
    }

    @GetMapping("/available")
    public ResponseEntity<List<ProgramDto>> getAvailablePrograms(Authentication authentication) {
        log.info("Request to get available programs");
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(programService.getAvailablePrograms(email));
    }

    @PostMapping("/{id}/status")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<ProgramDto> updateProgramStatus(
            @PathVariable Long id,
            @RequestBody ProgramStatusUpdateDto request) {
        log.info("Request to update program {} status to {}", id, request.getStatus());
        ProgramDto updatedProgram = programService.updateProgramStatus(id, request.getStatus());
        return ResponseEntity.ok(updatedProgram);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProgramDto> getProgramById(@PathVariable Long id, Authentication authentication) {
        log.info("Request to get program by id: {}", id);
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(programService.getProgramById(id, email));
    }

    @GetMapping("/{id}/enrolled")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<List<UserDTO>> getEnrolledUsers(@PathVariable Long id) {
        log.info("Request to get enrolled users for program id: {}", id);
        return ResponseEntity.ok(programService.getEnrolledUsers(id));
    }
}
