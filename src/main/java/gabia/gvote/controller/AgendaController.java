package gabia.gvote.controller;

import gabia.gvote.argumentresolver.Auth;
import gabia.gvote.dto.AgendaCreateRequestDTO;
import gabia.gvote.dto.AgendaCreateResponseDTO;
import gabia.gvote.dto.AgendaPageDTO;
import gabia.gvote.dto.SessionMemberAuthDTO;
import gabia.gvote.entity.MemberGubun;
import gabia.gvote.entity.VoteGubun;
import gabia.gvote.service.AgendaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AgendaController {

    private final static int MAX_REQUEST_PAGE_SIZE = 100;
    private final static String AGENDA_DELETE_MSG = "AGENDA DELETE SUCCESS!";

    private final AgendaService agendaService;


    @GetMapping("/agendas")
    public ResponseEntity<AgendaPageDTO> getAgendaPage(@Auth SessionMemberAuthDTO sessionMemberAuthDTO, Pageable pageable) {
        log.info("/agendas member auth: {}", sessionMemberAuthDTO);
        validatePageSize(pageable);
        AgendaPageDTO agendaPageDTO = agendaService.agendaPagination(pageable);
        return ResponseEntity.ok(agendaPageDTO);
    }

    @PostMapping("/agenda")
    public ResponseEntity<AgendaCreateResponseDTO> createAgenda(@Auth SessionMemberAuthDTO sessionMemberAuthDTO, @Valid @RequestBody AgendaCreateRequestDTO agendaCreateRequestDTO) {
        validateMemberGubunIsAdmin(sessionMemberAuthDTO);
        validateAgendaCreateDTO(agendaCreateRequestDTO);
        Long savedId = agendaService.create(sessionMemberAuthDTO, agendaCreateRequestDTO);
        return new ResponseEntity(new AgendaCreateResponseDTO(savedId), HttpStatus.CREATED);
    }

    @DeleteMapping("/agenda/{agendaId}")
    public ResponseEntity deleteAgenda(@Auth SessionMemberAuthDTO sessionMemberAuthDTO, @PathVariable Long agendaId) {
        validateMemberGubunIsAdmin(sessionMemberAuthDTO);
        agendaService.delete(agendaId);
        return ResponseEntity.ok(AGENDA_DELETE_MSG);
    }

    private void validateAgendaCreateDTO(AgendaCreateRequestDTO agendaCreateRequestDTO) {
        if (agendaCreateRequestDTO.getVoteGubun().equals(VoteGubun.UNLIMITED) && agendaCreateRequestDTO.getAvailableVoteCount() != null) {
            throw new IllegalArgumentException("????????? ??????????????? ?????? ?????? ?????? ????????? ??? ????????????.");
        }
    }

    private void validatePageSize(Pageable pageable) {
        if (pageable.getPageSize() > MAX_REQUEST_PAGE_SIZE) {
            throw new IllegalArgumentException("?????? ????????? ???????????? ?????? ?????????.");
        }
    }

    private void validateMemberGubunIsAdmin(SessionMemberAuthDTO sessionMemberAuthDTO) {
        if (sessionMemberAuthDTO.getMemberGubun().equals(MemberGubun.ADMIN)) {
            return;
        }
        throw new IllegalArgumentException("?????????????????? ????????? ???????????????.");
    }

}
