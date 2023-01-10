package gabia.gvote;

import gabia.gvote.entity.*;
import gabia.gvote.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

@Profile("local")
@RequiredArgsConstructor
@Component
public class DataInit {

    private final MemberRepository memberRepository;
    private final MemberDetailRepository memberDetailRepository;
    private final MemberAuthRepository memberAuthRepository;
    private final AgendaRepository agendaRepository;
    private final VoteRepository voteRepository;

    @PostConstruct
    public void init() {
        LocalDateTime now = LocalDateTime.now();

        // 일반회원
        Member normalMember = Member.builder()
                .remainVoteCount(100L)
                .createdAt(now)
                .updatedAt(now)
                .build();
        memberRepository.save(normalMember);

        MemberDetail normalMemberDetail = MemberDetail.builder()
                .member(normalMember)
                .memberEmail("temp_normal_email@gmail.com")
                .memberName("Summer_normal")
                .memberAvatarImageUrl("www.gabia.com/img1")
                .createdAt(now)
                .updatedAt(now)
                .build();
        memberDetailRepository.save(normalMemberDetail);

        MemberAuth normalMemberAuth = MemberAuth.builder()
                .member(normalMember)
                .memberGubun(MemberGubun.NORMAL)
                .memberStringId("ckdals123")
                .memberStringPw("123123")
                .build();
        memberAuthRepository.save(normalMemberAuth);


        // 관리자 회원
        Member adminMember = Member.builder()
                .remainVoteCount(100L)
                .createdAt(now)
                .updatedAt(now)
                .build();
        memberRepository.save(adminMember);

        MemberDetail adminMemberDetail = MemberDetail.builder()
                .member(adminMember)
                .memberEmail("temp_admin_email@gmail.com")
                .memberName("Summer_admin")
                .memberAvatarImageUrl("www.gabia.com/img2")
                .createdAt(now)
                .updatedAt(now)
                .build();
        memberDetailRepository.save(adminMemberDetail);

        MemberAuth adminMemberAuth = MemberAuth.builder()
                .member(adminMember)
                .memberGubun(MemberGubun.NORMAL)
                .memberStringId("admin123")
                .memberStringPw("123123")
                .build();
        memberAuthRepository.save(adminMemberAuth);


        Agenda agenda1 = Agenda.builder()
                .member(adminMember)
                .agendaSubject("init agenda subject1")
                .agendaContent("init content1")
                .createdAt(now)
                .updatedAt(now)
                .build();

        Agenda agenda2 = Agenda.builder()
                .member(adminMember)
                .agendaSubject("init agenda subject2")
                .agendaContent("init content2")
                .createdAt(now)
                .updatedAt(now)
                .build();
        agendaRepository.save(agenda1);
        agendaRepository.save(agenda2);

        // 무제한투표
        Vote vote1 = Vote.builder()
                .agenda(agenda1)
                .voteStatus(VoteStatus.ING)
                .voteGubun(VoteGubun.UNLIMITED)
                .remainAvailableVoteCount(100L)
                .startAt(LocalDateTime.of(2022, 01, 01, 01, 01, 01))
                .closeAt(LocalDateTime.of(2024, 01, 01, 01, 01, 01))
                .createdAt(now)
                .updatedAt(now)
                .build();

        // 선착순 투표
        Vote vote2 = Vote.builder()
                .agenda(agenda2)
                .voteStatus(VoteStatus.ING)
                .voteGubun(VoteGubun.LIMITED)
                .remainAvailableVoteCount(100L)
                .startAt(LocalDateTime.of(2022, 01, 01, 01, 01, 01))
                .closeAt(LocalDateTime.of(2024, 01, 01, 01, 01, 01))
                .createdAt(now)
                .updatedAt(now)
                .build();
        voteRepository.save(vote1);
        voteRepository.save(vote2);

    }


}
