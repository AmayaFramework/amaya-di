package io.github.amayaframework.di.scheme;

import java.lang.reflect.Member;

public class IllegalMemberException extends RuntimeException {
    private final Member member;

    public IllegalMemberException(String message, Member member) {
        super(message);
        this.member = member;
    }

    public Member getMember() {
        return member;
    }
}
