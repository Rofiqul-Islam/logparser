package com.cloudhubs.logparser.model;


/**
 * @author Md Rofiqul Islam
 * Model for hold class data
 */
public class ClassObject {
    private String objectName;
    private String className;
    private String classPath;

    public ClassObject(String className, String classPath,String objectName) {
        this.className = className;
        this.classPath = classPath;
        this.objectName = objectName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }
}
