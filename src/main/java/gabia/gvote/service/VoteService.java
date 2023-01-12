package gabia.gvote.service;

import gabia.gvote.dto.VoteAdminResponseDTO;
import gabia.gvote.dto.VoteCreateRequestDTO;
import gabia.gvote.dto.VoteNormalResponseDTO;
import gabia.gvote.dto.VoteResponseDTO;
import gabia.gvote.entity.*;
import gabia.gvote.repository.MemberAuthRepository;
import gabia.gvote.repository.MemberRepository;
import gabia.gvote.repository.VoteHistoryRepository;
import gabia.gvote.repository.VoteRepository;
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
    private final MemberAuthRepository memberAuthRepository;
    private final MemberRepository memberRepository;
    private final VoteHistoryService voteHistoryService;

    private final EntityManager entityManager;

    // TODO: 캐싱 고민
    @Transactional(readOnly = true)
    public VoteResponseDTO findOne(Long memberId, Long voteId) {
        MemberAuth memberAuth = memberAuthRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 id의 회원이 존재하지 않습니다."));

        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 id의 투표가 존재하지 않습니다."));
        validateVoteStatusIsDone(vote);

        List<VoteHistory> voteHistories = voteHistoryRepository.findAllByReferenceVoteId(voteId);
        Map<VoteHistoryActionGubun, Long> statistics = calculateCommonVoteHistoryStatistics(voteHistories);

        if (memberAuth.getMemberGubun().equals(MemberGubun.NORMAL)) {
            return VoteNormalResponseDTO.of(voteId, statistics);
        }
        return VoteAdminResponseDTO.of(voteId, statistics, voteHistories);
    }

    @Transactional
    public Long close(Long memberId, Long voteId) {
        MemberAuth memberAuth = memberAuthRepository.findMemberAuthWithMemberByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원 id 입니다."));
        MemberAuth.validateMemberIsAdmin(memberAuth);

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

    private Map<VoteHistoryActionGubun, Long> calculateCommonVoteHistoryStatistics(List<VoteHistory> voteHistories) {
        return voteHistories.stream()
                .collect(groupingBy(VoteHistory::getVoteHistoryActionGubun, summingLong(VoteHistory::getVoteCount)));
    }

}

