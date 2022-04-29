package com.cloudhubs.logparser.model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Md Rofiqul Islam
 */
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
    Set<Log> logList = new HashSet<>();
    List<String> parameterList = new LinkedList<>();
    List<InvokedMethod> invokedMethodList = new LinkedList<>();
    List<ForStmtModel> forStmtList = new LinkedList<>();
    List<IfStmtModel> ifStmtModelList = new LinkedList<>();

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

    public Set<Log> getLogList() {
        return logList;
    }

    public void setLogList(Set<Log> logList) {
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

    public List<InvokedMethod> getInvokedMethodList() {
        return invokedMethodList;
    }

    public void setInvokedMethodList(List<InvokedMethod> invokedMethodList) {
        this.invokedMethodList = invokedMethodList;
    }

    @Override
    public String toString() {
        String str = methodName + "()\n"+
                classPath+"\n ---------------------------------------------------------------------------- \n Log list = { \n";
        for(Log l :logList){
            str = str + l.getLog().replace("\""," ")+"\n";
        }
        str =str+"\n }";
        return str;

    }

    public List<ForStmtModel> getForStmtList() {
        return forStmtList;
    }

    public void setForStmtList(List<ForStmtModel> forStmtList) {
        this.forStmtList = forStmtList;
    }

    public List<IfStmtModel> getIfStmtModelList() {
        return ifStmtModelList;
    }

    public void setIfStmtModelList(List<IfStmtModel> ifStmtModelList) {
        this.ifStmtModelList = ifStmtModelList;
    }
}
