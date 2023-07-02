package jog.util;

import java.util.StringJoiner;

import jog.ast.visitor.CodeGenUtil.ValType;

public class MiscUtil {
    public static String makeParamDeclList(
            Iterable<? extends CharSequence> params, ValType valType) {
        String type = valType.getTypeName();
        StringJoiner sj = new StringJoiner(", " + type + " ", type + " ", "");
        params.forEach(sj::add);
        return sj.toString();
    }
}
