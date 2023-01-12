package gabia.gvote.controller;

import gabia.gvote.argumentresolver.Auth;
import gabia.gvote.dto.VoteCloseResponseDTO;
import gabia.gvote.dto.VoteCreateRequestDTO;
import gabia.gvote.dto.VoteResponseDTO;
import gabia.gvote.dto.VoteSuccessResponseDTO;
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
    public ResponseEntity<VoteResponseDTO> findVote(@Auth Long memberId, @PathVariable Long voteId) {
        return ResponseEntity.ok(voteService.findOne(memberId, voteId));
    }

    @PatchMapping("/vote/{voteId}")
    public ResponseEntity<VoteCloseResponseDTO> closeVote(@Auth Long memberId, @PathVariable Long voteId) {
        Long closedVoteId = voteService.close(memberId, voteId);
        return ResponseEntity.ok(new VoteCloseResponseDTO(closedVoteId));
    }

    @PostMapping("/vote/{voteId}")
    public ResponseEntity<VoteSuccessResponseDTO> vote(@Auth Long memberId, @PathVariable Long voteId, @Valid @RequestBody VoteCreateRequestDTO voteCreateRequestDTO) {
        voteService.vote(memberId, voteId, voteCreateRequestDTO);
        return ResponseEntity.ok(new VoteSuccessResponseDTO(voteId));
    }

}
