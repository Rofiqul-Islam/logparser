package com.cloudhubs.logparser.service;

import com.cloudhubs.logparser.model.ClassMethods;
import com.cloudhubs.logparser.model.MethodLog;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.springframework.stereotype.Service;
import com.google.common.base.Strings;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

@Service
public class CrawlerService {
    String[] strArr = {".info", ".debug", ".warn", ".error", ".fatal", ".trace", ".all"};
    public List<ClassMethods> listMethodCalls(File projectDir) {
        List<ClassMethods>  classMethodsList = new LinkedList<>();
        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            ClassMethods classMethods = new ClassMethods(path,new LinkedList<>());
            System.out.println(path);
            System.out.println(Strings.repeat("=", path.length()));
            try {
                CompilationUnit cu = StaticJavaParser.parse(file, StandardCharsets.UTF_8);
                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(MethodDeclaration n, Object arg) {
                        super.visit(n, arg);
                        //System.out.println(" [L " + n.getBegin() + "] " + n);
                        System.out.println(n.getDeclarationAsString());
                        List<String> logList = new LinkedList<>();
                        StringTokenizer str = new StringTokenizer(n.toString(), ";");
                        while (str.hasMoreElements()) {
                            String temp = str.nextToken();
                            for (String s : strArr) {
                                if (temp.contains(s)) {
                                    while (temp.contains("{")) {
                                        temp = temp.substring(temp.indexOf("{") + 1);
                                        //System.out.println(temp);
                                    }
                                    temp = temp.replace('}', ' ').trim();
                                    logList.add(temp);
                                }
                            }
                        }
                        for (String s : logList) {
                            System.out.println(s);
                        }
                        MethodLog methodLog = new MethodLog(n.getDeclarationAsString(),logList);
                        classMethods.getMethodList().add(methodLog);
                        System.out.println("--------------------------");
                    }
                }.visit(cu,null);
            } catch (FileNotFoundException e ) {
                e.printStackTrace();
            }
            System.out.println(); // empty line
            classMethodsList.add(classMethods);
        }).explore(projectDir);
        return classMethodsList;
    }
}

