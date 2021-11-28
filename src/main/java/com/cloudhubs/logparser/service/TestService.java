package com.cloudhubs.logparser.service;

import com.cloudhubs.logparser.model.ClassMethods;
import com.cloudhubs.logparser.model.MethodLog;
import com.cloudhubs.logparser.model.MethodModel;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.common.base.Strings;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

@Service
public class TestService {

    public String temp;

    public List<MethodModel> check(File projectDir) {
        List<MethodModel> methodModelList = new LinkedList<>();
        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            System.out.println(path);
            System.out.println(Strings.repeat("=", path.length()));
            try {
                CompilationUnit cu = StaticJavaParser.parse(file, StandardCharsets.UTF_8);
                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(ClassOrInterfaceDeclaration n, Object arg) {
                        super.visit(n, arg);
                        String[] result = n.asClassOrInterfaceDeclaration().toString().split("\n", 2);
                        setTemp(result[0]);
                    }
                }.visit(cu, null);
                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(MethodDeclaration n, Object arg) {
                        super.visit(n, arg);
                        //System.out.println(n.getDeclarationAsString() + ":");
                        MethodModel methodModel = new MethodModel(n.getDeclarationAsString(),n.getNameAsString(), path, n.getBegin().get().line, n.getEnd().get().line);

                        test(temp + "\n" + n + "}", methodModel);
                        methodModelList.add(methodModel);
                    }
                }.visit(cu, null);
                //System.out.println("****************************************");
                //System.out.println("****************************************");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //System.out.println(); // empty line
        }).explore(projectDir);
        return methodModelList;
    }

    public void test(String code, MethodModel m) {
        //System.out.println(code);
        try {
            CompilationUnit cu = StaticJavaParser.parse(code);
            new VoidVisitorAdapter<Object>() {
                @Override
                public void visit(AssignExpr n, Object arg) {
                    super.visit(n, arg);
                    m.getModifiedVariableList().add(n.getTarget().toString().replace("this.", ""));
                }
            }.visit(cu, null);
            new VoidVisitorAdapter<Object>() {
                @Override
                public void visit(VariableDeclarationExpr n, Object arg) {
                    super.visit(n, arg);

                    //System.out.println(n.);
                    List<VariableDeclarator> myVars = n.getVariables();
                    for (VariableDeclarator vars : myVars) {
                        //System.out.println("Variable Name: " + vars.getName());
                        m.getVariableList().add(vars.getNameAsString());
                        //m.getRICList().removeAll(m.getModifiedVariableList());
                        try {
                            //System.out.println(vars.getBegin());
                            //System.out.println(vars.getName() + " = " + vars.getInitializer().get());

                        } catch (Exception e) {

                        }

                    }
                }
            }.visit(cu, null);


        } catch (Exception e) {

        }
    }

    public void setTemp(String str) {
        this.temp = str;
    }





}
