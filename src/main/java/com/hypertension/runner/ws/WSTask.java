package com.hypertension.runner.ws;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @Author Avirup
 *
 * Base class for tasks
 * Calls method using java reflection with the parameter Map
 *
 * Parameter map should be with static structure
 */
public class WSTask<T> {

    final WSType wsType;
    final String methodToExecute;
    final Map<String, Object> paramMap;

    /**
     * result contains the execution result
     */
    private T result;

    /**
     * @param wsType
     * @param methodToExecute
     * @param paramMap
     *
     * @usage Constructor to set parameters from subclass
     */
    public WSTask(WSType wsType, String methodToExecute, Map<String, Object> paramMap) {
        this.wsType = wsType;
        this.methodToExecute = methodToExecute;
        this.paramMap = paramMap;
    }

    /**
     * @throws NoSuchMethodException
     * @throws java.lang.reflect.InvocationTargetException
     * @throws IllegalAccessException
     *
     * execution method calling from WS task executor
     */
    public void execute() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        result = (T) getMethod(methodToExecute).invoke(this, paramMap);
    }

    private Method getMethod(final String methodName) throws NoSuchMethodException {
        return this.getClass().getDeclaredMethod(methodName, new Class[] {Map.class});
    }

    public T getResult() {
        return result;
    }

    public static class WsTaskInputType {
        public static final String WS_INPUT = "WS_INPUT";
        public static final String BASE_URI = "BASE_URI";
    }
}
