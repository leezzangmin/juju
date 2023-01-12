package gabia.gvote.service;

import gabia.gvote.dto.VoteCreateRequestDTO;
import gabia.gvote.entity.Member;
import gabia.gvote.entity.Vote;
import gabia.gvote.entity.VoteHistory;
import gabia.gvote.repository.VoteHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class VoteHistoryService {

    private final VoteHistoryRepository voteHistoryRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public Long writeHistory(Vote vote, Member member, VoteCreateRequestDTO voteCreateRequestDTO) {
        VoteHistory voteHistory = VoteHistory.builder()
                .vote(vote)
                .member(member)
                .voteHistoryActionGubun(voteCreateRequestDTO.getVoteHistoryActionGubun())
                .voteCount(voteCreateRequestDTO.getVoteCount())
                .build();

        Long savedId = voteHistoryRepository.save(voteHistory).getVoteHistoryId();
        return savedId;
    }
}
