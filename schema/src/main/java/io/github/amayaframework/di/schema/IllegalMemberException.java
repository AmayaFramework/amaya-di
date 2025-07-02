package io.github.amayaframework.di.schema;

import java.lang.reflect.Member;

/**
 * Thrown to indicate that some member of the class, intended as a target for the injection scheme, cannot be one.
 */
public class IllegalMemberException extends RuntimeException {
    private final Member member;

    /**
     * Constructs an {@link IllegalMemberException} with the specified detail message and illegal member.
     *
     * @param message the detail message
     * @param member  the illegal member
     */
    public IllegalMemberException(String message, Member member) {
        super(message);
        this.member = member;
    }

    /**
     * Returns the illegal class member.
     *
     * @return the illegal class member
     */
    public Member getMember() {
        return member;
    }
}
