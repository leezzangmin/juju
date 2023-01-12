package gabia.gvote.entity;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public enum VoteStatus {
    BEFORE,
    ING,
    DONE;

    public static VoteStatus calculate(LocalDateTime current, LocalDateTime startAt, LocalDateTime closeAt) {
        if (startAt.isAfter(current)) {
            return VoteStatus.BEFORE;
        }
        if (startAt.isBefore(current) && closeAt.isAfter(current)) {
            return VoteStatus.ING;
        }
        return VoteStatus.DONE;
    }
}
