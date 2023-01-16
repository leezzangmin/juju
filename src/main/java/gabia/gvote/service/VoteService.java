package gabia.gvote.service;

import gabia.gvote.dto.*;
import gabia.gvote.entity.*;
import gabia.gvote.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingLong;


@RequiredArgsConstructor
@Service
public class VoteService {

    private final VoteRepository voteRepository;
    private final VoteHistoryRepository voteHistoryRepository;
    private final MemberRepository memberRepository;
    private final VoteHistoryService voteHistoryService;
    private final VoteResultRepository voteResultRepository;
    private final NamedLockRepository namedLockRepository;

    private final EntityManager entityManager;

    @Transactional
    public VoteResponseDTO adminFindOne(Long voteId) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 id의 투표가 존재하지 않습니다."));
        validateVoteStatusIsDone(vote);

        namedLockRepository.getNamedLock(voteId.toString());

        if (voteResultRepository.existsByReferenceVoteId(voteId)) {
            namedLockRepository.releaseNamedLock(voteId.toString());
            return voteResultRepository.findByReferenceVoteIdSimpleAdminDTO(voteId);
        }

        VoteResult voteResult = calculateVote(voteId);
        voteResultRepository.save(voteResult);

        namedLockRepository.releaseNamedLock(voteId.toString());
        return VoteAdminResponseDTO.of(voteResult);
    }

    @Transactional
    public VoteResponseDTO findOne(Long voteId) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 id의 투표가 존재하지 않습니다."));
        validateVoteStatusIsDone(vote);

        namedLockRepository.getNamedLock(voteId.toString());

        if (voteResultRepository.existsByReferenceVoteId(voteId)) {
            namedLockRepository.releaseNamedLock(voteId.toString());
            return voteResultRepository.findByReferenceVoteIdSimpleNormalDTO(voteId);
        }

        VoteResult voteResult = calculateVote(voteId);
        voteResultRepository.save(voteResult);

        namedLockRepository.releaseNamedLock(voteId.toString());
        return VoteNormalResponseDTO.of(voteResult);
    }


    @Transactional
    public Long close(Long voteId) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 투표 id 입니다"));
        vote.closeVote();

        return vote.getVoteId();
    }

    @Transactional
    public Long vote(Long memberId, Long voteId, VoteCreateRequestDTO voteCreateRequestDTO) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원 id 입니다."));

        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 투표 id 입니다"));
        validateVoteStatusIsIng(vote);

        if (vote.getVoteGubun().equals(VoteGubun.LIMITED)) {
            decreaseVoteCountWithLock(voteId, voteCreateRequestDTO.getVoteCount());
        }

        member.decreaseVoteCount(voteCreateRequestDTO.getVoteCount());
        voteHistoryService.writeHistory(vote, member, voteCreateRequestDTO);
        return voteId;
    }

    private void decreaseVoteCountWithLock(Long voteId, Long voteCount) {
        entityManager.clear();
        Vote vote = voteRepository.findByIdLock(voteId).get();
        vote.minusAvailableVoteCount(voteCount);
    }

    private void validateVoteStatusIsIng(Vote vote) {
        if (!vote.calculateCurrentVoteStatus().equals(VoteStatus.ING)) {
            throw new IllegalStateException("진행중인 투표가 아닙니다.");
        }
    }

    private void validateVoteStatusIsDone(Vote vote) {
        if (!vote.calculateCurrentVoteStatus().equals(VoteStatus.DONE)) {
            throw new IllegalStateException("아직 종료되지 않은 투표는 조회할 수 없습니다.");
        }
    }

    private VoteResult calculateVote(Long voteId) {
        List<VoteHistory> voteHistories = voteHistoryRepository.findAllByReferenceVoteId(voteId);
        Vote vote = voteRepository.findById(voteId).get();
        Map<VoteHistoryActionGubun, Long> statistics = calculateCommonVoteHistoryStatistics(voteHistories);

        return VoteAdminResponseDTO.toEntity(vote, statistics, voteHistories);
    }

    private Map<VoteHistoryActionGubun, Long> calculateCommonVoteHistoryStatistics(List<VoteHistory> voteHistories) {
        return voteHistories.stream()
                .collect(groupingBy(VoteHistory::getVoteHistoryActionGubun, summingLong(VoteHistory::getVoteCount)));
    }

}

