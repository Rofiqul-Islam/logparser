package com.cloudhubs.logparser.model;

import java.util.LinkedList;
import java.util.List;

public class InvokedMethod {
    private String methodName;
    private String classPath;

    public InvokedMethod(String methodName, String classPath) {
        this.methodName = methodName;
        this.classPath = classPath;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

}
