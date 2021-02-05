package com.cloudhubs.logparser.service;

import com.github.javaparser.ast.Node;
import org.springframework.stereotype.Service;

public class NodeIterator {
    public interface NodeHandler {
        boolean handle(Node node);
    }
    private NodeHandler nodeHandler;
    public NodeIterator(NodeHandler nodeHandler) {
        this.nodeHandler = nodeHandler;
    }
    public void explore(Node node) {
        if (nodeHandler.handle(node)) {
            for (Node child : node.getChildNodes()) {
                explore(child);
            }
        }
    }
}