package com.ninyo.common.rest.utils;

import java.util.function.Function;

public class FunctionUtils {

    public static <T, R, E extends Exception> Function<T, R> throwingFunctionWrapper(FunctionWithExceptions<T, R, E> function) {
        return o -> {
            try {
                return function.apply(o);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        };
    }

    @FunctionalInterface
    public interface FunctionWithExceptions<T, R, E extends Exception> {
        R apply(T t) throws E;
    }
}
