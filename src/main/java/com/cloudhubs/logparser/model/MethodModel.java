package com.cloudhubs.logparser.model;

import com.github.javaparser.ast.type.Type;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class MethodModel {
    private String id;
    private String methodDefination;
    private String methodName;
    private String classPath;
    private int methodBegin;
    private int methodEnd;
    Set<String> modifiedVariableList = new HashSet<>();
    Set<String> RICList = new HashSet<>();
    Set<String> variableList  = new HashSet<>();
    List<String> logList = new LinkedList<>();
    List<String> parameterList = new LinkedList<>();
    List<String> invokedMethodList = new LinkedList<>();

    public MethodModel(String methodDefination, String methodName, String classPath, int methodBegin, int methodEnd) {
        this.methodName = methodName;
        this.classPath = classPath;
        this.methodBegin = methodBegin;
        this.methodEnd = methodEnd;
        this.methodDefination = methodDefination;
    }

    public void calculateId(){
        this.id = this.classPath+"_"+this.methodName+"_"+this.getParameterList().size();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getMethodBegin() {
        return methodBegin;
    }

    public void setMethodBegin(int methodBegin) {
        this.methodBegin = methodBegin;
    }

    public int getMethodEnd() {
        return methodEnd;
    }

    public void setMethodEnd(int methodEnd) {
        this.methodEnd = methodEnd;
    }

    public Set<String> getModifiedVariableList() {
        return modifiedVariableList;
    }

    public void setModifiedVariableList(Set<String> modifiedVariableList) {
        this.modifiedVariableList = modifiedVariableList;
    }

    public Set<String> getRICList() {
        return RICList;
    }

    public void setRICList(Set<String> RICList) {
        this.RICList = RICList;
    }

    public Set<String> getVariableList() {
        return variableList;
    }

    public void setVariableList(Set<String> variableList) {
        this.variableList = variableList;
    }

    public List<String> getLogList() {
        return logList;
    }

    public void setLogList(List<String> logList) {
        this.logList = logList;
    }

    public String getMethodDefination() {
        return methodDefination;
    }

    public void setMethodDefination(String methodDefination) {
        this.methodDefination = methodDefination;
    }

    public List<String> getParameterList() {
        return parameterList;
    }

    public void setParameterList(List<String> parameterList) {
        this.parameterList = parameterList;
    }

    public List<String> getInvokedMethodList() {
        return invokedMethodList;
    }

    public void setInvokedMethodList(List<String> invokedMethodList) {
        this.invokedMethodList = invokedMethodList;
    }
}
