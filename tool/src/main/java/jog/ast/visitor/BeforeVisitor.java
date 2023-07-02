package jog.ast.visitor;

import jog.ast.expr.BinNode;
import jog.ast.expr.ConNode;
import jog.ast.expr.CGExpr;
import jog.ast.expr.LitNode;
import jog.ast.expr.UnaryNode;
import jog.ast.expr.VarNode;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

public class BeforeVisitor extends ExprVisitorAdapter {

    // Will be reset to {@code true} after the first use, because we
    // only want to ignore the first operator check at the root node,
    // e.g., for (x + 1) + 2 we want to ignore checking the second
    // "+".
    private boolean checkOperator = false;

    private boolean addedSameNodeCheck = false;

    private final Set<CGExpr> conNodesChecked = new HashSet<>();

    private final StringJoiner result = new StringJoiner("\n    && ");

    private final Map<CGExpr, String> nodeToCodeGen;

    private final Map<CGExpr, List<String>> nodeToCodeGenList;

    public BeforeVisitor(Map<CGExpr, String> nodeToCodeGen,
            Map<CGExpr, List<String>> nodeToCodeGenList) {
        this.nodeToCodeGen = nodeToCodeGen;
        this.nodeToCodeGenList = nodeToCodeGenList;
    }

    public String getResult() {
        if (!addedSameNodeCheck) {
            addSameNodeCheck();
            addedSameNodeCheck = true;
        }
        return result.toString();
    }

    @Override
    public void visit(BinNode binNode, Void arg) {
        String nodeCode = nodeToCodeGen.get(binNode);
        if (checkOperator) {
            // make relation "{nodeCode}->Opcode() == Op_XXX"
            String cond = CodeGenUtil.makeOperatorCheck(nodeCode, binNode.getOperator(), binNode.getValType());
            result.add(cond);
        } else {
            checkOperator = true;
        }
        binNode.getLeft().accept(this, arg);
        binNode.getRight().accept(this, arg);
    }

    @Override
    public void visit(ConNode conNode, Void arg) {
        // We want to check only once for every ConNode even when it
        // has more than one use.
        if (conNodesChecked.add(conNode)) {
            String nodeCode = nodeToCodeGen.get(conNode);
            // make relation "{nodeCode}->Opcode() == Op_ConI"
            result.add(CodeGenUtil.makeIsCon(nodeCode, conNode.getValType()));
        }
    }

    @Override
    public void visit(VarNode varNode, Void arg) {
        // do nothing
    }

    @Override
    public void visit(LitNode litNode, Void arg) {
        // We want to check only once for every LitNode even when it
        // has more than one use.
        if (conNodesChecked.add(litNode)) {
            String nodeCode = nodeToCodeGen.get(litNode);
            // make relation such as "{nodeCode} == TypeInt::MINUS_1" or
            // "phase->type({nodeCode})->isa_int()->is_con({value})"
            result.add(CodeGenUtil.makeIsConLiteral(nodeCode, litNode.getValue(), litNode.getValType()));
        }
    }

    @Override
    public void visit(UnaryNode unaryNode, Void arg) {
        // not seen, for now.
    }


    private void addSameNodeCheck() {
        for (Map.Entry<CGExpr, List<String>> e : nodeToCodeGenList.entrySet()) {
            List<String> codeList = e.getValue();
            for (int i = 1; i < codeList.size(); i++) {
                // add an equality equation between every code and the
                // first one
                result.add(CodeGenUtil.makeEqual(codeList.get(0), codeList.get(i)));
            }
        }
    }
}
