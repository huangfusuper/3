package com.baizhi.doubledemo;

import java.io.Serializable;
import java.util.Arrays;

public class MethodInvokeMeta implements Serializable {
    private String method;
    private Class<?>[] parameterType;
    private Object[] args;
    private String targetClass;

    @Override
    public String toString() {
        return "MethodInvokeMeta{" +
                "method='" + method + '\'' +
                ", parameterType=" + Arrays.toString(parameterType) +
                ", args=" + Arrays.toString(args) +
                ", targetClass='" + targetClass + '\'' +
                '}';
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Class<?>[] getParameterType() {
        return parameterType;
    }

    public void setParameterType(Class<?>[] parameterType) {
        this.parameterType = parameterType;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public String getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(String targetClass) {
        this.targetClass = targetClass;
    }

    public MethodInvokeMeta() {
    }

    public MethodInvokeMeta(String method, Class<?>[] parameterType, Object[] args, String targetClass) {
        this.method = method;
        this.parameterType = parameterType;
        this.args = args;
        this.targetClass = targetClass;
    }
}
