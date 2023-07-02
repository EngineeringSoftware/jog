package jog.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Where a pattern is obtained from.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Origin {
    // enum Type {
    //     OPENJDK("OpenJDK"),
    //     LLVM("LLVM");
    //
    //     private final String origin;
    //     private Type(String origin) {
    //         this.origin = origin;
    //     }
    // }

    String value();
}
