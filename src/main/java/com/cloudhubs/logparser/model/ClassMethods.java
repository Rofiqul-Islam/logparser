package com.cloudhubs.logparser.model;

import java.util.List;

public class ClassMethods {
    private String classPath;
    private List<MethodLog> methodList;

    public ClassMethods(String classPath, List<MethodLog> methodList) {
        this.classPath = classPath;
        this.methodList = methodList;
    }

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public List<MethodLog> getMethodList() {
        return methodList;
    }

    public void setMethodList(List<MethodLog> methodList) {
        this.methodList = methodList;
    }
}
