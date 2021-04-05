package com.cloudhubs.logparser.model;

public class Log {
    private String log;
    private int line;

    public Log(String log, int line) {
        this.log = log;
        this.line = line;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }
}
