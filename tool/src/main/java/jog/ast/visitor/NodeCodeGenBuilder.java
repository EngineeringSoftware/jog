package jog.ast.visitor;

import jog.Constants;
import jog.ast.expr.BinNode;
import jog.ast.expr.ConNode;
import jog.ast.expr.CGExpr;
import jog.ast.expr.LitNode;
import jog.ast.expr.UnaryNode;
import jog.ast.expr.VarNode;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NodeCodeGenBuilder extends ExprVisitorAdapter {

    // new variable -> in() function call chain
    private final Map<String, String> varToInCallChain = new LinkedHashMap<>();

    // a node -> code generated to represent the node
    // e.g. VarNode(x) -> in(1)->in(1) in "(x + 1) + 2"
    // For the node with more than one use, we pick the shortest
    // code generated, e.g. before((x + y) + x)
    // VarNode(x) -> [in(1)->in(1), in(2)] we use in(2).
    private final Map<CGExpr, String> nodeToUniqueCodeGen = new HashMap<>();

    // Some node can have more than one use, e.g. before((x + y) + x)
    // VarNode(x) -> [in(1)->in(1), in(2)]
    private final Map<CGExpr, List<String>> nodeToCodeGenList = new LinkedHashMap<>(); // keep insertion-order

    // 1: left, 2: right
    private final Deque<Integer> sequence = new ArrayDeque<>();

    public Map<CGExpr, String> getNodeToCodeGen() {
        return nodeToUniqueCodeGen;
    }

    public Map<CGExpr, List<String>> getNodeToCodeGenList() {
        return nodeToCodeGenList;
    }

    public Map<String, String> getVarToInCallChain() {
        return varToInCallChain;
    }

    @Override
    public void visit(BinNode binNode, Void arg) {
        addTheNodeCodeMapping(binNode);
        sequence.addLast(1); // push left
        binNode.getLeft().accept(this, arg);
        popSeq();
        sequence.addLast(2); // push right
        binNode.getRight().accept(this, arg);
        popSeq();
    }

    @Override
    public void visit(ConNode conNode, Void arg) {
        addTheNodeCodeMapping(conNode);
    }

    @Override
    public void visit(VarNode varNode, Void arg) {
        addTheNodeCodeMapping(varNode);
    }

    @Override
    public void visit(LitNode litNode, Void arg) {
        addTheNodeCodeMapping(litNode);
    }

    @Override
    public void visit(UnaryNode unaryNode, Void arg) {
        // not expected
    }

    private void addTheNodeCodeMapping(CGExpr node) {
        if (sequence.isEmpty()) {
            // do not add root node
            return;
        }
        String var = seqToVar();
        String inCallChain = seqToCode();
        if (!varToInCallChain.containsKey(var)) {
            varToInCallChain.put(var, inCallChain);
        }
        List<String> codeGenList = nodeToCodeGenList.getOrDefault(node, new ArrayList<>());
        codeGenList.add(var);
        nodeToCodeGenList.putIfAbsent(node, codeGenList);

        if (!nodeToUniqueCodeGen.containsKey(node)
                || varToInCallChain.get(nodeToUniqueCodeGen.get(node)).length() > inCallChain.length()) {
            // update with shorter code generated
            nodeToUniqueCodeGen.put(node, var);
        }
    }

    private String seqToVar() {
        return seqToVar(sequence);
    }

    public static String seqToVar(Deque<Integer> seq) {
        // seq would not be empty
        return seq.stream().map(i -> Integer.toString(i)) // map to "1" or "2"
                .collect(Collectors.joining("", Constants.UNIQUE_PREFIX + "in", ""));
    }

    private String seqToCode() {
        return seqToCode(sequence);
    }

    public static String seqToCode(Deque<Integer> seq) {
        // sequence would be empty
        if (seq.size() == 1) {
            return "in(" + seq.getLast() + ")";
        }
        Deque<Integer> prevSeq = new ArrayDeque<>(seq);
        prevSeq.removeLast();
        // var != NULL && <n> < var->req() ? var->in(<n>) : NULL
        int n = seq.getLast();
        String var = seqToVar(prevSeq);
        return var + " != NULL && " + n + " < " + var + "->req()" + " ? " + var + "->in(" + n + ") : NULL";
    }

    private void popSeq() {
        if (!sequence.isEmpty()) {
            sequence.removeLast();
        }
    }
}
