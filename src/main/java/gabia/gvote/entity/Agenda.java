package gabia.gvote.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@Entity
public class Agenda {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long agendaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agenda_reference_member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String agendaSubject;

    @Column(nullable = false)
    private String agendaContent;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

}
