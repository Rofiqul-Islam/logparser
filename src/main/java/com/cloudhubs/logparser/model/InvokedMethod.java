package com.cloudhubs.logparser.model;

import java.util.LinkedList;
import java.util.List;

public class InvokedMethod {
    private String methodId;
    private Integer calledLine;

    public InvokedMethod(String methodId, Integer calledLine) {
        this.methodId = methodId;
        this.calledLine = calledLine;
    }

    public String getMethodId() {
        return methodId;
    }

    public void setMethodId(String methodId) {
        this.methodId = methodId;
    }

    public Integer getCalledLine() {
        return calledLine;
    }

    public void setCalledLine(Integer calledLine) {
        this.calledLine = calledLine;
    }
}
