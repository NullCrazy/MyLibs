package com.luckincoffee.base;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static java.lang.reflect.Proxy.newProxyInstance;

/**
 * @author xingguo.lei
 */
final class NoOp {

    private static final InvocationHandler DEFAULT_VALUE = new DefaultValueInvocationHandler();

    private NoOp() {
        // no instances
    }

    public static <T> T of(Class<T> interfaceClass) {
        return (T) newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass},
                DEFAULT_VALUE);
    }

    private static class DefaultValueInvocationHandler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return method.getReturnType();
        }
    }
}