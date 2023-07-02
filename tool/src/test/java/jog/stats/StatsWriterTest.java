package jog.stats;

import com.amihaiemil.eoyaml.YamlMapping;
import jog.ast.expr.BinNode;
import jog.ast.visitor.CodeGenUtil.ValType;
import jog.codegen.PatternV;
import jog.codegen.PatternFile;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatsWriterTest {

    private static Map<String, PatternV> patternsByName;
    private StatsWriter statsWriter;
    private Stats stats;
    private YamlMapping mapping;

    @BeforeClass
    public static void setUpClass() {
        List<PatternFile> patternFiles = List.of(
                new PatternFile("src/test/java/AddNodeExample.java"),
                new PatternFile("src/test/java/SubNodeExample.java")
        );
        patternsByName = new HashMap<>();
        for (PatternFile pf : patternFiles) {
            for (PatternV p : pf.getPatterns()) {
                patternsByName.put(p.getStats().getName(), p);
            }
        }
    }

    @Test
    public void testAOpenJDKPattern() {
        setStats("pAdd1");
        assertKeyValueIsScalar("name", "pAdd1");
        assertKeyValueIsScalar("before", "(CON1 - x) + CON2");
        assertKeyValueIsScalar("after", "(CON1 + CON2) - x");
        assertKeyValueIsScalar("origin", "OpenJDK");
        assertKeyValueIsScalar("type", BinNode.NodeType.ADD_NODE.toCodeGen(ValType.INT));
        assertKeyValueIsEmptyList("shadows");
        assertKeyValueIsEmptyList("composites");
        assertKeyValueIsScalar("pr", "-1");
        assertKeyValueIsScalar("pattern", stats.getPatternCode());
        assertKeyValueIsScalar("generated", stats.getGeneratedCode());
    }

    @Test
    public void testALLVMPattern() {
        setStats("pNewAddAddSub1156");
        assertKeyValueIsScalar("name", "pNewAddAddSub1156");
        assertKeyValueIsScalar("before", "x + x");
        assertKeyValueIsScalar("after", "x << 1");
        assertKeyValueIsScalar("origin", "LLVM");
        assertKeyValueIsScalar("type", BinNode.NodeType.ADD_NODE.toCodeGen(ValType.INT));
        assertKeyValueIsEmptyList("shadows");
        assertKeyValueIsEmptyList("composites");
        assertKeyValueIsScalar("pr", "6675");
        assertKeyValueIsScalar("pattern", stats.getPatternCode());
        assertKeyValueIsScalar("generated", stats.getGeneratedCode());
    }

    @Test
    public void testPatternWithShadowedPatterns() {
        setStats("pAdd2");
        assertKeyValueIsScalar("name", "pAdd2");
        assertKeyValueIsScalar("before", "(a - b) + (c - d)");
        assertKeyValueIsScalar("after", "(a + c) - (b + d)");
        assertKeyValueIsScalar("origin", "OpenJDK");
        assertKeyValueIsScalar("type", BinNode.NodeType.ADD_NODE.toCodeGen(ValType.INT));
        assertKeyValueIsList("shadows", "pAdd5", "pAdd6", "pNewAddAddSub1165");
        assertKeyValueIsEmptyList("composites");
        assertKeyValueIsScalar("pr", "-1");
        assertKeyValueIsScalar("pattern", stats.getPatternCode());
        assertKeyValueIsScalar("generated", stats.getGeneratedCode());
    }

    private void setStats(String patternName) {
        stats = patternsByName.get(patternName).getStats();
        statsWriter = new StatsWriter(stats);
        mapping = statsWriter.toYAML().asSequence().yamlMapping(0);
    }

    private YamlMapping toYAMLMapping() {
        return statsWriter.toYAML().asSequence().yamlMapping(0);
    }

    private void assertKeyValueIsScalar(String key, String value) {
        Assert.assertEquals(value, mapping.value(key).asScalar().value());
    }

    private void assertKeyValueIsEmptyList(String key) {
        Assert.assertTrue(mapping.value(key).asSequence().values().isEmpty());
    }

    private void assertKeyValueIsList(String key, String... values) {
        Assert.assertEquals(List.of(values),
                mapping.value(key).asSequence().values().stream()
                        .map(e -> e.asScalar().value()).collect(Collectors.toList()));
    }
}
