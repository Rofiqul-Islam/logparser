package com.cloudhubs.logparser.service;

import com.cloudhubs.logparser.model.*;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.springframework.stereotype.Service;
import com.google.common.base.Strings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class LogFinderService {
    String[] strArr = {".info", ".debug", ".warn", ".error", ".fatal", ".trace", ".all"};
    public String temp;
    Map<String,MethodModel> methodModelMap = new HashMap<>();   // holds all methodmodel
    List<String> classList = new LinkedList<>();
    Set<ClassObject> classObjectSet = new HashSet<>();
    Map<String, List<InvokedMethod>> calledMethodMap = new HashMap<>();   // holds all called method
    Map<Integer, String> methodIdMapper = new HashMap<>();


    public Map<String,MethodModel> findAllClass(File projectDir){
        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            ClassMethods classMethods = new ClassMethods(path, new LinkedList<>());
            classList.add(path);

        }).explore(projectDir);
        listMethodCalls(projectDir);
        check(projectDir);
        Integer methodCounter = 0;
        for(String key: methodModelMap.keySet()){
            methodModelMap.get(key).setInvokedMethodList(calledMethodMap.get(key));
            methodIdMapper.put(methodCounter, key);
            methodCounter++;
        }
        generateGraph(methodModelMap);
        return methodModelMap;

    }



    public void check(File projectDir) {

        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            try {
                CompilationUnit cu = StaticJavaParser.parse(file, StandardCharsets.UTF_8);
                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(ClassOrInterfaceDeclaration n, Object arg) {
                        super.visit(n, arg);
                        setTemp("public class "+n.getNameAsString()+"{");
                    }
                }.visit(cu, null);
                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(MethodDeclaration n, Object arg) {
                        super.visit(n, arg);
                        MethodModel methodModel = new MethodModel(n.getDeclarationAsString(),n.getNameAsString(), path, n.getBegin().get().line, n.getEnd().get().line);
                        for(Parameter p: n.getParameters()){
                          //System.out.println(p.getType().asString());
                            methodModel.getParameterList().add(p.getType().asString());
                        }
                        methodModel.calculateId();
                        //parsingMethodBody(n.getBody().get().toString(),methodModel);
                        test(temp + "\n" + n + "}", methodModel);
                      //  System.out.println(n.getNameAsString()+" :- ");
                        methodVisitor(temp + "\n" + n + "}",methodModel);
                        listmethodLogs(cu, methodModel);
                        methodModelMap.put(methodModel.getId(),methodModel);

                    }
                }.visit(cu, null);
                //System.out.println("****************************************");
                //System.out.println("****************************************");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //System.out.println(); // empty line
        }).explore(projectDir);
        //listMethodCalls(projectDir);
    }

    public void test(String code, MethodModel m) {
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
                    List<VariableDeclarator> myVars = n.getVariables();
                    for (VariableDeclarator vars : myVars) {
                        m.getVariableList().add(vars.getNameAsString());
                        m.getRICList().add(vars.getNameAsString());
                        m.getRICList().removeAll(m.getModifiedVariableList());

                    }
                }
            }.visit(cu, null);


        } catch (Exception e) {

        }
    }

    public void setTemp(String str) {
        this.temp = str;
    }


    public void listmethodLogs(CompilationUnit cu, MethodModel m){
        new VoidVisitorAdapter<Object>() {

            @Override
            public void visit(MethodDeclaration n, Object arg) {
                super.visit(n, arg);
                StringTokenizer str = new StringTokenizer(n.toString(), ";");
                int counter = 0;
                while (str.hasMoreElements()) {
                    counter++;
                    String temp = str.nextToken();
                    for (String s : strArr) {
                        if (temp.contains(s)) {
                            while (temp.contains("{")) {
                                temp = temp.substring(temp.indexOf("{") + 1);
                            }
                            temp = temp.replace('}', ' ').trim();

                            m.getLogList().add("line - "+n.getBegin().get().line+" - " + temp);
                        }
                    }
                }
            }
        }.visit(cu, null);
    }




 //-----------------------------------------
    public List<ClassMethods> listMethodCalls(File projectDir) {
        List<ClassMethods> classMethodsList = new LinkedList<>();
        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            ClassMethods classMethods = new ClassMethods(path, new LinkedList<>());
            System.out.println(path);
            System.out.println(Strings.repeat("=", path.length()));
            try {
                CompilationUnit cu = StaticJavaParser.parse(file, StandardCharsets.UTF_8);
                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(VariableDeclarationExpr n, Object arg) {
                        super.visit(n, arg);
                        for(VariableDeclarator v:n.getVariables()){
                            for(String s: classList){
                                if(s.contains(v.getType().asString()+".java")){
                                    //System.out.println(v.getType()+" - "+s);
                                    classObjectSet.add(new ClassObject(v.getType().asString(), s,v.getNameAsString()));
                                }
                            }
                        }

                    }
                }.visit(cu, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(); // empty line
            classMethodsList.add(classMethods);
        }).explore(projectDir);
        return classMethodsList;
    }




    public void methodVisitor(String code, MethodModel m){
        CompilationUnit cu = StaticJavaParser.parse(code);
        new VoidVisitorAdapter<Object>() {
            int counter = 0;
            @Override
            public void visit(MethodCallExpr n, Object arg) {
                super.visit(n, arg);
                StringTokenizer str = new StringTokenizer(n.asMethodCallExpr().toString(),".");
                String tempObj = str.nextToken();
                int flag =0;
                for(ClassObject clo:classObjectSet){
                    if(clo.getObjectName().equals(tempObj)){
                        flag =1;
                        if(calledMethodMap.get(m.getId())==null){
                            List<InvokedMethod> temp  =  new LinkedList<>();
                            String methodId =clo.getClassPath()+"_"+n.getName()+"_"+n.getArguments().size();
                            temp.add(new InvokedMethod(methodId,n.getBegin().get().line));
                            calledMethodMap.put(m.getId(),temp);
                        }else{
                            List<InvokedMethod> temp = calledMethodMap.get(m.getId());
                            String methodId =clo.getClassPath()+"_"+n.getName()+"_"+n.getArguments().size();
                            temp.add(new InvokedMethod(methodId,n.getBegin().get().line));
                            calledMethodMap.put(m.getId(),temp);
                        }
                        break;
                    }
                }
            if(flag==0){
                if(calledMethodMap.get(m.getId())==null){
                    List<InvokedMethod> temp  =  new LinkedList<>();
                    String methodId =m.getClassPath()+"_"+n.getName()+"_"+n.getArguments().size();
                    temp.add(new InvokedMethod(methodId,n.getBegin().get().line));
                    calledMethodMap.put(m.getId(),temp);
                }else{
                    List<InvokedMethod> temp = calledMethodMap.get(m.getId());
                    String methodId =m.getClassPath()+"_"+n.getName()+"_"+n.getArguments().size();
                    temp.add(new InvokedMethod(methodId,n.getBegin().get().line));
                    calledMethodMap.put(m.getId(),temp);
                }
            }

            }
        }.visit(cu, null);
    }


    public void generateGraph(Map<String, MethodModel> methodModelMap){
        StringBuilder graph = new StringBuilder();
        graph.append("digraph cil_rad {").append("\n");
        graph.append("rankdir = LR;").append("\n");
        graph.append("node [shape=oval];").append("\n");
        for(String key: methodModelMap.keySet()){
            MethodModel m  = methodModelMap.get(key);
            if(m.getInvokedMethodList()!=null){
                List<InvokedMethod> temp = m.getInvokedMethodList();
                for(InvokedMethod im:temp){
                    try {
                        //graph.append("  {"+key+"} -> {"+im.getMethodId()+"};\n");
                        graph.append("  " + m.getId().hashCode()+ " -> " + methodModelMap.get(im.getMethodId()).getId().hashCode()+"[label =\" called line = "+im.getCalledLine()+ "\"];\n");
                        graph.append("  " + m.getId().hashCode()+" [ label = \" "+m.toString()+"\" ]"+ ";\n");
                        graph.append(methodModelMap.get(im.getMethodId()).getId().hashCode()+" [ label = \" " +m.toString()+"\" ]"+ ";\n");
                        //System.out.println(methodModelMap.get(s).getMethodName());

                    }catch (Exception e){

                    }
                }
            }
        }
        graph.append("}");
        try {
            FileWriter writer = new FileWriter("Output.dot");
            writer.write(String.valueOf(graph));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}

