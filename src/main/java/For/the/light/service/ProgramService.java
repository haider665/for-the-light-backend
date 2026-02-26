package For.the.light.service;

import For.the.light.dto.ProgramDto;
import For.the.light.dto.UserDTO;
import For.the.light.entity.Program;
import For.the.light.entity.User;
import For.the.light.repository.ProgramRepository;
import For.the.light.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import For.the.light.entity.ProgramStatus;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class ProgramService {

    private final ProgramRepository programRepository;
    private final UserRepository userRepository;

    @Transactional
    public ProgramDto createProgram(ProgramDto programDto, String createdByEmail) {
        User user = userRepository.findByEmail(createdByEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Program program;
        if (programDto.getId() != null) {
            program = programRepository.findById(programDto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Program not found"));
        } else {
            program = new Program();
            program.setCreatedBy(user);
        }

        program.setTitle(programDto.getTitle());
        program.setDescription(programDto.getDescription());
        program.setDisplayImage(programDto.getDisplayImage());
        program.setProgramSchedule(programDto.getProgramSchedule());
        program.setProgramStartDate(programDto.getProgramStartDate());
        program.setRegistrationDeadline(programDto.getRegistrationDeadline());

        if (programDto.getStatus() != null) {
            program.setStatus(programDto.getStatus());
        } else if (program.getId() == null) {
            program.setStatus(ProgramStatus.DRAFT);
        }

        Program savedProgram = programRepository.save(program);
        return mapToDto(savedProgram);
    }

    @Transactional
    public ProgramDto updateProgramStatus(Long programId, ProgramStatus status) {
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new IllegalArgumentException("Program not found"));

        program.setStatus(status);
        Program savedProgram = programRepository.save(program);
        return mapToDto(savedProgram);
    }

    @Transactional
    public void enrollUser(Long programId, String userEmail) {
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new IllegalArgumentException("Program not found"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        program.getEnrolledUsers().add(user);
        programRepository.save(program);
    }

    public List<ProgramDto> getAllPrograms(String currentUserEmail) {
        return programRepository.findAll().stream()
                .map(program -> mapToDto(program, currentUserEmail))
                .collect(Collectors.toList());
    }

    public List<ProgramDto> getAvailablePrograms(String currentUserEmail) {
        return programRepository
                .findAllByStatus(ProgramStatus.PUBLISHED)
                .stream()
                .map(program -> mapToDto(program, currentUserEmail))
                .collect(Collectors.toList());
    }

    public ProgramDto getProgramById(Long id, String currentUserEmail) {
        Program program = programRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Program not found"));
        return mapToDto(program, currentUserEmail);
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getEnrolledUsers(Long programId) {
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new IllegalArgumentException("Program not found"));

        return program.getEnrolledUsers().stream()
                .map(this::mapToUserDto)
                .collect(Collectors.toList());
    }

    private UserDTO mapToUserDto(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setPicture(user.getPicture());
        dto.setRoles(user.getRoles().stream().map(Enum::name).collect(Collectors.toList()));
        dto.setCreatedAt(user.getCreatedAt());
        dto.setLastLogin(user.getLastLogin());
        return dto;
    }

    private ProgramDto mapToDto(Program program) {
        return mapToDto(program, null);
    }

    private ProgramDto mapToDto(Program program, String currentUserEmail) {
        ProgramDto dto = new ProgramDto();
        dto.setId(program.getId());
        dto.setTitle(program.getTitle());
        dto.setDescription(program.getDescription());
        dto.setDisplayImage(program.getDisplayImage());
        dto.setProgramSchedule(program.getProgramSchedule());
        dto.setProgramStartDate(program.getProgramStartDate());
        dto.setRegistrationDeadline(program.getRegistrationDeadline());
        dto.setStatus(program.getStatus());
        dto.setCreatedBy(program.getCreatedBy() != null ? program.getCreatedBy().getEmail() : null);
        dto.setVersion(program.getVersion());
        dto.setCreatedAt(program.getCreatedAt());
        dto.setUpdatedAt(program.getUpdatedAt());

        if (currentUserEmail != null) {
            boolean isEnrolled = program.getEnrolledUsers().stream()
                    .anyMatch(user -> user.getEmail().equals(currentUserEmail));
            dto.setEnrolled(isEnrolled);
        }

        return dto;
    }
}
