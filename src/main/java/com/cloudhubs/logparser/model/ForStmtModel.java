package com.cloudhubs.logparser.model;

import java.util.LinkedList;
import java.util.List;

public class ForStmtModel {
    List<Log> logList= new LinkedList<>();
    private int startLine;
    private int endLine;

    public ForStmtModel(int startLine, int endLine) {
        this.startLine = startLine;
        this.endLine = endLine;
    }

    public List<Log> getLogList() {
        return logList;
    }

    public void setLogList(List<Log> logList) {
        this.logList = logList;
    }

    public int getStartLine() {
        return startLine;
    }

    public void setStartLine(int startLine) {
        this.startLine = startLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }
}
