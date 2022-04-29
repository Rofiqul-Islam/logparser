package com.cloudhubs.logparser.model;

import java.util.List;


/**
 * @author Md Rofiqul Islam
 */
public class MethodLog {
    private String methodName;
    private List<String> logList;

    public MethodLog(String methodName, List<String> logList) {
        this.methodName = methodName;
        this.logList = logList;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<String> getLogList() {
        return logList;
    }

    public void setLogList(List<String> logList) {
        this.logList = logList;
    }
}
