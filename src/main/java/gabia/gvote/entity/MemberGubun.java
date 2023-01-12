package gabia.gvote.entity;

import lombok.Getter;

@Getter
public enum MemberGubun {
    NORMAL,
    ADMIN,
    DORMANCY; // 휴면

    public static boolean isAdmin(MemberAuth memberAuth) {
        if (memberAuth.getMemberGubun().equals(MemberGubun.ADMIN)) {
            return true;
        }
        return false;
    }
}
