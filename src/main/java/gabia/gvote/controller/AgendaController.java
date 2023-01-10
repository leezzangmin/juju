package gabia.gvote.controller;

import gabia.gvote.argumentresolver.Auth;
import gabia.gvote.dto.AgendaPageDTO;
import gabia.gvote.service.AgendaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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


    private void validatePageSize(Pageable pageable) {
        if (pageable.getPageSize() > MAX_REQUEST_PAGE_SIZE) {
            throw new IllegalArgumentException("요청 페이지 사이즈가 너무 큽니다.");
        }
    }

}
