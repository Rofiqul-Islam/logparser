package com.cloudhubs.logparser.service;

import com.cloudhubs.logparser.model.*;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.LocalClassDeclarationStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    ConverterService converterService;


    String[] strArr = {".info", ".debug", ".warn", ".error", ".fatal", ".trace", ".all"};
    public String temp;
    Map<String, MethodModel> methodModelMap = new HashMap<>();   // holds all methodmodel
    List<String> classList = new LinkedList<>();
    Map<String, Set<ClassObject>> classObjectMap = new HashMap<>();
    //Set<ClassObject> classObjectSet = new HashSet<>();
    Map<String, List<InvokedMethod>> calledMethodMap = new HashMap<>();   // holds all called method
    Map<Integer, String> methodIdMapper = new HashMap<>();
    List<String> topMethodList = new LinkedList<>();

    FileWriter myWriter;
    int clusterNumber = 0;



    public Map<String, MethodModel> findAllClass(File projectDir) {
        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            ClassMethods classMethods = new ClassMethods(path, new LinkedList<>());
            classList.add(path);

        }).explore(projectDir);
        listMethodCalls(projectDir);
        check(projectDir);
        Integer methodCounter = 0;
        for (String key : methodModelMap.keySet()) {
            methodModelMap.get(key).setInvokedMethodList(calledMethodMap.get(key));
            methodIdMapper.put(methodCounter, key);
            methodCounter++;
        }
        generateGraph();
        csvGenerator();
        converterService.csvToXes("log.csv");


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
                        setTemp("public class " + n.getNameAsString() + "{");
                    }
                }.visit(cu, null);
                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(MethodDeclaration n, Object arg) {
                        super.visit(n, arg);
                        MethodModel methodModel = new MethodModel(n.getDeclarationAsString(), n.getNameAsString(), path, n.getBegin().get().line, n.getEnd().get().line);
                        for (Parameter p : n.getParameters()) {
                            //System.out.println(p.getType().asString());
                            methodModel.getParameterList().add(p.getType().asString());
                        }
                        methodModel.calculateId();
                        //parsingMethodBody(n.getBody().get().toString(),methodModel);
                        findVariable(temp + "\n" + n + "}", methodModel);
                        //  System.out.println(n.getNameAsString()+" :- ");
                        methodVisitor(temp + "\n" + n + "}", methodModel);
                        methodModelMap.put(methodModel.getId(), methodModel);

                    }
                }.visit(cu, null);
                listmethodLogs(cu, path);
                findConditionalStmt(cu);
                //System.out.println("****************************************");
                //System.out.println("****************************************");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //System.out.println(); // empty line
        }).explore(projectDir);
        //listMethodCalls(projectDir);
    }

    public void findConditionalStmt(CompilationUnit cu) {
        new VoidVisitorAdapter<Object>() {
            @Override
            public void visit(ForStmt n, Object arg) {
                super.visit(n, arg);
                //System.out.println("For - " + n.asForStmt());
            }
        }.visit(cu, null);

        new VoidVisitorAdapter<Object>() {
            @Override
            public void visit(IfStmt n, Object arg) {
                super.visit(n, arg);
                //System.out.println("If - " + n.asIfStmt());
                //System.out.println("xyz " + n.getElseStmt());
            }
        }.visit(cu, null);
    }

    public void findVariable(String code, MethodModel m) {
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

            new VoidVisitorAdapter<Object>() {
                @Override
                public void visit(ForEachStmt n, Object arg) {
                    super.visit(n, arg);
                    ForStmtModel tempForStmt = new ForStmtModel(n.getBegin().get().line, n.getEnd().get().line);
                    StringTokenizer str = new StringTokenizer(n.toString(), ";");
                    //System.out.println(n+"-"+str.countTokens());
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
                                if (temp != null) {
                                    tempForStmt.getLogList().add(new Log(temp.substring(temp.indexOf("\"") + 1, temp.lastIndexOf("\"")), counter));
                                }
                            }
                        }
                    }
                    m.getForStmtList().add(tempForStmt);
                }
            }.visit(cu, null);

            new VoidVisitorAdapter<Object>() {
                @Override
                public void visit(IfStmt n, Object arg) {
                    super.visit(n, arg);
                    IfStmtModel tempIfStmt = new IfStmtModel();
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
                                if (temp != null) {
                                    tempIfStmt.getLogList().add(new Log(temp.substring(temp.indexOf("\"") + 1, temp.lastIndexOf("\"")), counter));
                                }

                            }
                        }
                    }
                    m.getIfStmtModelList().add(tempIfStmt);
                }
            }.visit(cu, null);


        } catch (Exception e) {

        }
    }

    public void setTemp(String str) {
        this.temp = str;
    }


    public void listmethodLogs(CompilationUnit cu, String path) {
        new VoidVisitorAdapter<Object>() {

            @Override
            public void visit(MethodDeclaration n, Object arg) {
                super.visit(n, arg);
                StringTokenizer str = new StringTokenizer(n.toString(), ";");
                //System.out.println(n+"-"+str.countTokens());
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
                            String tempKey = path + "_" + n.getNameAsString() + "_" + n.getParameters().size();
                            MethodModel m = methodModelMap.get(tempKey);
                            if (m != null) {
                                m.getLogList().add(new Log(temp.substring(temp.indexOf("\"") + 1, temp.lastIndexOf("\"")), counter));
                            } else {
                                System.out.println(tempKey);
                            }
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
                    public void visit(VariableDeclarator n, Object arg) {
                        super.visit(n, arg);

                        for (String s : classList) {
                            if (s.contains(n.getType().asString() + ".java")) {
                                //System.out.println(v.getType()+" - "+s);
                                if (classObjectMap.get(path) == null) {
                                    classObjectMap.put(path, new HashSet<>());
                                }
                                classObjectMap.get(path).add(new ClassObject(n.getType().asString(), s, n.getNameAsString()));
                                // classObjectMap.add(new ClassObject(n.getType().asString(), s,n.getNameAsString()));
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


    public void methodVisitor(String code, MethodModel m) {

        CompilationUnit cu = StaticJavaParser.parse(code);
        new VoidVisitorAdapter<Object>() {
            int counter = 0;

            @Override
            public void visit(MethodCallExpr n, Object arg) {
                super.visit(n, arg);
                //System.out.println(n);
                StringTokenizer str = new StringTokenizer(n.asMethodCallExpr().toString(), ".");
                String tempObj = str.nextToken();
                int flag = 0;
                if (classObjectMap.get(m.getClassPath()) != null) {
                    for (ClassObject clo : classObjectMap.get(m.getClassPath())) {
                        if (clo.getObjectName().equals(tempObj)) {
                            flag = 1;
                            if (calledMethodMap.get(m.getId()) == null) {
                                List<InvokedMethod> temp = new LinkedList<>();
                                String methodId = clo.getClassPath() + "_" + n.getName() + "_" + n.getArguments().size();
                                //System.out.println(methodId);
                                temp.add(new InvokedMethod(methodId, n.getBegin().get().line));
                                calledMethodMap.put(m.getId(), temp);
                            } else {
                                List<InvokedMethod> temp = calledMethodMap.get(m.getId());
                                String methodId = clo.getClassPath() + "_" + n.getName() + "_" + n.getArguments().size();
                                temp.add(new InvokedMethod(methodId, n.getBegin().get().line));
                                calledMethodMap.put(m.getId(), temp);
                            }
                            break;
                        }
                    }
                }
           /* if(flag==0){
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
            }*/

            }
        }.visit(cu, null);
    }


    public void topMethodFinding() {
        for (String key : methodModelMap.keySet()) {
            int flag = 0;
            for (String tempKey : methodModelMap.keySet()) {
                MethodModel temp = methodModelMap.get(tempKey);
                if (temp.getInvokedMethodList() != null && temp.getInvokedMethodList().contains(key)) {
                    flag = 1;
                    break;
                }
            }
            if (flag == 0) {
                topMethodList.add(key);
            }
        }
    }


    void csvGenerator() {
        topMethodFinding();
        try {
            myWriter = new FileWriter("log.csv");
            int caseId = 0;
            for (String key : topMethodList) {
                int eventId = 0;
                recursion(key, caseId, eventId);
                caseId++;
            }
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    List<String> clusterList =  new ArrayList<>();

    public void generateGraph() {
        topMethodFinding();

        FileWriter myWriter = null;
        try {
            myWriter = new FileWriter("business_technicaldebt.dot");
            myWriter.write("strict digraph G {\n node [shape=box];\n" +
                    "rankdir=LR;\n" +
                    "size=\"50,50\"; ratio=fill;\n");
            for (String key : topMethodList) {
                if(methodModelMap.get(key)!=null) {
                    String res = "";
                    res = invokedMethodTraverse(key, res);
                    if(res.length()>0) {
                        myWriter.write("Start"+res+" -> end;\n");
                    }
                }
            }
            for(String str:clusterList){
                myWriter.write(str);
            }
            myWriter.write("Start [shape=Mdiamond];\n" +
                    "\tend [shape=Msquare];\n}");
            myWriter.flush();
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String invokedMethodTraverse(String key, String res) throws IOException {
        MethodModel m = methodModelMap.get(key);
        Integer max = 0;
        Map<Integer, Object> tempMap = new HashMap<>();
        if (m != null) {
            if (m.getLogList() != null) {
                for (Log log : m.getLogList()) {
                    if (max < log.getLine()) {
                        max = log.getLine();
                    }
                    tempMap.put(log.getLine(), log.getLog());

                }
            }
            if (m.getInvokedMethodList() != null) {
                for (InvokedMethod invokeMethod : m.getInvokedMethodList()) {
                    if (max < invokeMethod.getCalledLine()) {
                        max = invokeMethod.getCalledLine();
                    }
                    tempMap.put(invokeMethod.getCalledLine(), invokeMethod);
                    //recursion(invokeMethod.getMethodId(), caseId);
                }
            }
            List<String> cluster = new ArrayList<>();
           // int flag =0;
            for (int i = 0; i <= max; i++) {
                if (tempMap.get(i) != null) {
                    Object x = tempMap.get(i);
                    if (x instanceof String) {
                        String str = x.toString().replaceAll(" ","_").replaceAll("'","");
                        /*if(flag==1){
                            myWriter.write(temp);
                            flag=0;
                        }*/
                        //temp = str;
                       res+=" -> "+str;
                        cluster.add(str.toString());

                    } else if (x instanceof InvokedMethod) {
                        //flag=1;
                        invokedMethodTraverse(((InvokedMethod) x).getMethodId(), res);
                    }
                }
            }

            if(cluster.size()>0) {
                String subgraph = "subgraph cluster_" +clusterNumber+ " {\n\t style=filled;\n" +
                        "\t\tcolor=lightgrey;\n\t\tnode [style=filled,color=white];\n";
                for (String st : cluster) {
                    subgraph += st + ", ";
                }
                subgraph = subgraph.substring(0, subgraph.length() - 2);
                subgraph += ";\n";

                subgraph += " \t\tlabel=\"" + m.getClassPath().replaceAll("/", "_").replaceAll(".java", "").substring(1).replaceAll("src_main_edu_baylor_","") + "_" + m.getMethodName() + "\";\n}\n";
                clusterList.add(subgraph);
                clusterNumber++;
            }

        }
        return res;
    }


    int recursion(String key, int caseId, int eventId) throws IOException {
        MethodModel m = methodModelMap.get(key);
        Integer max = 0;
        Map<Integer, Object> tempMap = new HashMap<>();
        if (m != null) {
            if (m.getLogList() != null) {
                for (Log log : m.getLogList()) {
                    if (max < log.getLine()) {
                        max = log.getLine();
                    }
                    tempMap.put(log.getLine(), log.getLog());
                    //myWriter.write(caseId + ";" + log.getLine()+";"+log.getLog());
                }
            }
            if (m.getInvokedMethodList() != null) {
                for (InvokedMethod invokeMethod : m.getInvokedMethodList()) {
                    if (max < invokeMethod.getCalledLine()) {
                        max = invokeMethod.getCalledLine();
                    }
                    tempMap.put(invokeMethod.getCalledLine(), invokeMethod);
                    //recursion(invokeMethod.getMethodId(), caseId);
                }
            }
            for (int i = 0; i <= max; i++) {
                if (tempMap.get(i) != null) {
                    Object x = tempMap.get(i);
                    if (x instanceof String) {
                        myWriter.write(caseId + ";" + eventId + ";" + x + "\n");
                        eventId++;
                    } else if (x instanceof InvokedMethod) {
                        eventId = recursion(((InvokedMethod) x).getMethodId(), caseId, eventId);
                    }
                }
            }

        }
        return eventId;
    }
}

