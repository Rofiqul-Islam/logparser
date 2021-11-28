package com.cloudhubs.logparser.model;

import java.util.LinkedList;
import java.util.List;

public class IfStmtModel {
    List<Log> logList = new LinkedList<>();

    public List<Log> getLogList() {
        return logList;
    }

    public void setLogList(List<Log> logList) {
        this.logList = logList;
    }
}
