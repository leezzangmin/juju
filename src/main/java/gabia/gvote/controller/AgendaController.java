package gabia.gvote.controller;

import gabia.gvote.argumentresolver.Auth;
import gabia.gvote.dto.AgendaCreateRequestDTO;
import gabia.gvote.dto.AgendaCreateResponseDTO;
import gabia.gvote.dto.AgendaPageDTO;
import gabia.gvote.entity.VoteGubun;
import gabia.gvote.service.AgendaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AgendaController {

    private final static int MAX_REQUEST_PAGE_SIZE = 100;

    private final AgendaService agendaService;


    @GetMapping("/agendas")
    public ResponseEntity<AgendaPageDTO> getAgendaPage(@Auth Long memberId, Pageable pageable) {
        log.info("/agendas memberId: {}", memberId);
        validatePageSize(pageable);
        AgendaPageDTO agendaPageDTO = agendaService.agendaPagination(pageable);
        return ResponseEntity.ok(agendaPageDTO);
    }

    @PostMapping("/agenda")
    public ResponseEntity<AgendaCreateResponseDTO> createAgenda(@Auth Long memberId, @Valid @RequestBody AgendaCreateRequestDTO agendaCreateRequestDTO) {
        validateAgendaCreateDTO(agendaCreateRequestDTO);
        Long savedId = agendaService.create(memberId, agendaCreateRequestDTO);
        return new ResponseEntity(new AgendaCreateResponseDTO(savedId), HttpStatus.CREATED);
    }

    private void validateAgendaCreateDTO(AgendaCreateRequestDTO agendaCreateRequestDTO) {
        if (agendaCreateRequestDTO.getVoteGubun().equals(VoteGubun.UNLIMITED) && agendaCreateRequestDTO.getAvailableVoteCount() != null) {
            throw new IllegalArgumentException("무제한 투표에서는 가용 투표 수를 설정할 수 없습니다.");
        }
    }

    private void validatePageSize(Pageable pageable) {
        if (pageable.getPageSize() > MAX_REQUEST_PAGE_SIZE) {
            throw new IllegalArgumentException("요청 페이지 사이즈가 너무 큽니다.");
        }
    }

}
