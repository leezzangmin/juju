package gabia.gvote.controller;

import gabia.gvote.argumentresolver.Auth;
import gabia.gvote.dto.*;
import gabia.gvote.entity.MemberGubun;
import gabia.gvote.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class VoteController {

    private final VoteService voteService;

    @GetMapping("/vote/{voteId}")
    public ResponseEntity<VoteResponseDTO> findVote(@Auth SessionMemberAuthDTO sessionMemberAuthDTO, @PathVariable Long voteId) {
        if (sessionMemberAuthDTO.getMemberGubun().equals(MemberGubun.ADMIN)) {
            VoteResponseDTO voteResponseDTO = voteService.adminFindOne(voteId);
            return ResponseEntity.ok(voteResponseDTO);
        }
        VoteResponseDTO voteResponseDTO = voteService.findOne(voteId);
        return ResponseEntity.ok(voteResponseDTO);
    }

    @PatchMapping("/vote/{voteId}")
    public ResponseEntity<VoteCloseResponseDTO> closeVote(@Auth SessionMemberAuthDTO sessionMemberAuthDTO, @PathVariable Long voteId) {
        validateMemberGubunIsAdmin(sessionMemberAuthDTO);
        Long closedVoteId = voteService.close(voteId);
        return ResponseEntity.ok(new VoteCloseResponseDTO(closedVoteId));
    }

    @PostMapping("/vote/{voteId}")
    public ResponseEntity<VoteSuccessResponseDTO> vote(@Auth SessionMemberAuthDTO sessionMemberAuthDTO, @PathVariable Long voteId, @Valid @RequestBody VoteCreateRequestDTO voteCreateRequestDTO) {
        Long votedId = voteService.vote(sessionMemberAuthDTO.getMemberId(), voteId, voteCreateRequestDTO);
        return ResponseEntity.ok(new VoteSuccessResponseDTO(votedId));
    }

    private void validateMemberGubunIsAdmin(SessionMemberAuthDTO sessionMemberAuthDTO) {
        if (sessionMemberAuthDTO.getMemberGubun().equals(MemberGubun.ADMIN)) {
            return;
        }
        throw new IllegalArgumentException("관리자에게만 허용된 요청입니다.");
    }

}
