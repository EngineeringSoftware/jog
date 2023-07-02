package jog;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlNode;
import jog.ast.expr.BinNode;
import jog.codegen.ClassGen;
import jog.codegen.PatternV;
import jog.codegen.TestClassGen;
import jog.shadow.PatternRelation;
import jog.codegen.PatternFile;
import jog.stats.ShadowWriter;
import jog.stats.StatsWriter;
import jog.util.YamlUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    public static void main(String... args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("needs path of input file!");
        }

        // Initialize all patterns
        List<PatternV> patterns = new LinkedList<>();
        for (String srcPath : args) {
            PatternFile patternFile = new PatternFile(srcPath);
            patterns.addAll(patternFile.getPatterns());
        }

        // Code generation
        generateCode(patterns);

        // Test code generation
        generateTest(patterns);

        // Collect stats
        collectStats(patterns);

        // Analyze relations between patterns
        PatternRelation ps = new PatternRelation(patterns);
        ps.analyze();
        ps.output();
    }

    private static void generateCode(List<PatternV> patterns) {
        Map<BinNode.NodeType, List<PatternV>> patternsByType = new HashMap<>();
        for (PatternV p : patterns) {
            BinNode.NodeType type = p.getStats().getNodeType();
            patternsByType.putIfAbsent(type, new LinkedList<>());
            patternsByType.get(type).add(p);
        }
        for (Map.Entry<BinNode.NodeType, List<PatternV>> e : patternsByType.entrySet()) {
            new ClassGen(e.getKey(), e.getValue(), "gen-code").generateCode();
        }
    }

    // Generate a separate test class for every node type
    private static void generateTest(List<PatternV> patterns) {
        Map<BinNode.NodeType, List<PatternV>> patternsByType = new HashMap<>();
        for (PatternV p: patterns) {
            if (!canGenerateTest(p)) {
                continue;
            }
            p.createTestMethodGen();
            BinNode.NodeType type = p.getStats().getNodeType();
            patternsByType.putIfAbsent(type, new LinkedList<>());
            patternsByType.get(type).add(p);
        }
        for (Map.Entry<BinNode.NodeType, List<PatternV>> e : patternsByType.entrySet()) {
            new TestClassGen(e.getKey(), e.getValue(), "gen-tests").generateTestCode();
        }
        // Collect all test stats into a file.
        YamlNode yamlNode = Yaml.createYamlSequenceBuilder()
                .add(Yaml.createYamlMappingBuilder()
                        .add("classes", String.valueOf(patternsByType.size()))
                        .add("methods", String.valueOf(patternsByType.values().stream().mapToInt(List::size).sum()))
                        .build())
                .build();
        YamlUtil.writeYamlNodeToFile(yamlNode, "test-stats.yml");
    }

    /**
     * Check if a test can be generated for a pattern, basically
     * if it is from OpenJDK and has no preconditions.
     */
    private static boolean canGenerateTest(PatternV pattern) {
        // Generate test for only existing patterns from OpenJDK
        return pattern.getStats().getOrigin().equals(Constants.OPENJDK)
                && pattern.getPreconditions().isEmpty()
                // Some patterns have implicit preconditions that make
                // tests not pass
                && !pattern.getName().equals("p__XRShiftC0_AndY_LShiftC0")
                && !pattern.getName().equals("p__XURShiftC0_AndY_LShiftC0")
                && !pattern.getName().equals("p_XAndC0_RShiftC1")
                && !pattern.getName().equals("p__XLShiftZ_PlusY_URShiftZ");
    }

    private static void collectStats(List<PatternV> patterns) {
        // Collect all stats into a file
        StatsWriter sw = new StatsWriter(patterns.stream()
        .map(PatternV::getStats)
        .collect(Collectors.toList()));
        sw.output("pattern-stats.yml");

        // Collect all shadow stats into a file
        ShadowWriter shw = new ShadowWriter(patterns.stream()
        .map(PatternV::getStats)
        // select patterns with shadows.
        .filter(stats -> !stats.getShadows().isEmpty())
        .collect(Collectors.toList()));
        shw.output("shadow-stats.yml");
    }
}
