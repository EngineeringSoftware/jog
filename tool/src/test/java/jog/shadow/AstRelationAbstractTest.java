package jog.shadow;

import jog.codegen.PatternFile;
import jog.codegen.PatternV;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AstRelationAbstractTest {
    protected final Map<String, PatternV> patternsByName;

    public AstRelationAbstractTest() {
        patternsByName = new HashMap<>();
        for (String exampleFileName : List.of("AddNodeExample.java", "SubNodeExample.java")) {
            PatternFile patternFile =
                    new PatternFile("src/test/java/" + exampleFileName);
            for (PatternV p : patternFile.getPatterns()) {
                patternsByName.put(p.getStats().getName(), p);
            }
        }
    }
}
