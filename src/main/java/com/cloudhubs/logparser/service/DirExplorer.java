package com.cloudhubs.logparser.service;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * @author Md Rofiqul Islam
 */
public class  DirExplorer {
    public interface FileHandler {
        void handle(int level, String path, File file);
    }
    public interface Filter {
        boolean interested(int level, String path, File file);
    }
    private FileHandler fileHandler;
    private Filter filter;

    /**
     * Method for directory exploring
     * @param filter
     * @param fileHandler
     */
    public DirExplorer(Filter filter, FileHandler fileHandler) {
        this.filter = filter;
        this.fileHandler = fileHandler;
    }

    /**
     * Method for exploring root
     * @param root
     */
    public void explore(File root) {
        explore(0, "", root);
    }

    /**
     * Overloading method of explore
     * @param level
     * @param path
     * @param file
     */
    private void explore(int level, String path, File file) {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                explore(level + 1, path + "/" + child.getName(), child);
            }
        } else {
            if (filter.interested(level, path, file)) {
                fileHandler.handle(level, path, file);
            }
        }
    }
}