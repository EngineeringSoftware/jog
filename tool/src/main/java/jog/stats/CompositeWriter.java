package jog.stats;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlNode;
import com.github.javaparser.ast.expr.Expression;
import jog.codegen.PatternV;
import jog.util.YamlUtil;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CompositeWriter extends Writer<Stats> {

    public CompositeWriter(Stats... statses) {
        super(statses);
    }

    public CompositeWriter(List<Stats> states) {
        super(states);
    }

    @Override
    protected YamlNode toYAML(Stats compositingStats) {
        return toYAML(compositingStats, compositingStats.getShadows());
    }

    public static YamlNode toYAML(Stats compositingStats, Set<PatternV> compositedPatterns) {
        var ymb = Yaml.createYamlMappingBuilder()
                .add("compositing", Yaml.createYamlMappingBuilder()
                        .add("name", compositingStats.getName())
                        .add("after", compositingStats.getActionAfter().toString())
                        .add("precondition", YamlUtil.collectionToYAMLSeq(
                                compositingStats.getPreconditions().stream()
                                        .map(Expression::toString)
                                        .collect(Collectors.toList())))
                        .build());
        var sSeqBuilder = Yaml.createYamlSequenceBuilder();
        for (var pattern : compositedPatterns) {
            sSeqBuilder = sSeqBuilder.add(Yaml.createYamlMappingBuilder()
                    .add("name", pattern.getName())
                    .add("before", pattern.getStats().getActionBefore().toString())
                    .add("precondition", YamlUtil.collectionToYAMLSeq(
                            pattern.getStats().getPreconditions().stream()
                                    .map(Expression::toString)
                                    .collect(Collectors.toList())))
                    .build());
        }
        ymb = ymb.add("composited", sSeqBuilder.build());
        return ymb.build();
    }
}

