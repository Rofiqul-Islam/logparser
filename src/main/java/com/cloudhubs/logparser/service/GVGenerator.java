package com.cloudhubs.logparser.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Md Rofiqul Islam
 */
public class GVGenerator {
    /*public static void generate(RadResponseContext radResponseContext) throws IOException {
        Graph g = graph("rad").directed();
        for (RestFlow restFlow : radResponseContext.getRestFlowContext().getRestFlows()) {
            RestEntity server = restFlow.getServers().get(0);
            Node nodeFrom = node(restFlow.getClassName() + "." + restFlow.getMethodName());
            Node nodeTo = node(server.getClassName() + "." + server.getMethodName());
            g = g.with(nodeFrom.link(nodeTo));
        }
        Graphviz.fromGraph(g).render(Format.SVG).toFile(new File("C:\\Users\\das\\Desktop\\rad.svg"));
    }*/

    public static void generate(Map<Map<String, Map<String, String>>, Integer> FunctionsMap) throws IOException {

        StringBuilder graph = new StringBuilder();
        graph.append("digraph cil_rad {").append("\n");
        graph.append("rankdir = LR;").append("\n");
        graph.append("node [shape=oval];").append("\n");

        int clusterIndex = 0;

        FunctionsMap.forEach((nestedMap, value) -> {
            nestedMap.forEach((node1, secondNestedMap) -> {
                secondNestedMap.forEach((node2, node3) -> {

                    String subGraph = formatNodeName(node2);
                    String endPoint = formatNodeName(node3);
                    String subEndPoint = subGraph + "_" + endPoint;


                    graph.append("\n");

                    graph.append(String.format("subgraph cluster_%s {", subGraph)).append("\n");
                    graph.append("label=").append(addDoubleQuotations(node2)).append(";").append("\n");
                    graph.append(subEndPoint).append(String.format("[label=%s]",addDoubleQuotations(node3))).append(";").append("\n");
                    graph.append("}").append("\n");
                    graph.append("\n");

                    String link = String.format("%s  -> %s [label = %s ];", addDoubleQuotations(node1), addDoubleQuotations(subEndPoint), addDoubleQuotations(String.valueOf(value)));
                    graph.append(link).append("\n");
                });
            });
        });

        graph.append("}");


        try (PrintWriter out = new PrintWriter("example/example.dot")) {
            out.println(graph);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //    private static Map<String, Set<String>> getClusters(RadResponseContext radResponseContext) {
//        Map<String, Set<String>> clusters = new HashMap<>();
//
//        for (RestFlow restFlow : radResponseContext.getRestFlowContext().getRestFlows()) {
//            String nodeFrom = getFullMethodName(restFlow);
//            addToMap(clusters, addDoubleQuotations(restFlow.getResourcePath()), nodeFrom);
//
//            for (RestEntity server : restFlow.getServers()) {
//                String nodeTo = getFullMethodName(server);
//                addToMap(clusters, addDoubleQuotations(server.getResourcePath()), nodeTo);
//            }
//        }
//
//        return clusters;
//    }
//
    private static String addDoubleQuotations(String str) {
        return "\"" + str + "\"";
    }

    private static String formatNodeName(String str) {
        return str.replaceAll("[^a-zA-Z0-9_]", "_");
    }

    private static void addToMap(Map<String, Set<String>> m, String key, String value) {
        if (!m.containsKey(key)) {
            m.put(key, new HashSet<>());
        }
        m.get(key).add(value);
    }

//    private static String getLinkLabel(RestEntity restEntity) {
//        return addDoubleQuotations(restEntity.getHttpMethod() + " " + restEntity.getUrl());
//    }
//
//    private static String getFullMethodName(RestEntity restEntity) {
//        return addDoubleQuotations(restEntity.getClassName() + "." + restEntity.getMethodName());
//    }
//
//    private static String getFullMethodName(RestFlow restFlow) {
//        return addDoubleQuotations(restFlow.getClassName() + "." + restFlow.getMethodName());
//    }
}