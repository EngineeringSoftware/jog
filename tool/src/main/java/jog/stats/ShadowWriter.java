package jog.stats;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlNode;
import com.github.javaparser.ast.expr.Expression;
import jog.codegen.PatternV;
import jog.util.YamlUtil;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ShadowWriter extends Writer<Stats> {

    public ShadowWriter(Stats... statses) {
        super(statses);
    }

    public ShadowWriter(List<Stats> states) {
        super(states);
    }

    @Override
    protected YamlNode toYAML(Stats shadowingStats) {
        return toYAML(shadowingStats, shadowingStats.getShadows());
    }

    public static YamlNode toYAML(Stats shadowingStats, Set<PatternV> shadowedPatterns) {
        var ymb = Yaml.createYamlMappingBuilder()
                .add("shadowing", Yaml.createYamlMappingBuilder()
                        .add("name", shadowingStats.getName())
                        .add("before", shadowingStats.getActionBefore().toString())
                        .add("precondition", YamlUtil.collectionToYAMLSeq(
                                shadowingStats.getPreconditions().stream()
                                        .map(Expression::toString)
                                        .collect(Collectors.toList())))
                        .build());
        var sSeqBuilder = Yaml.createYamlSequenceBuilder();
        for (var pattern : shadowedPatterns) {
            sSeqBuilder = sSeqBuilder.add(Yaml.createYamlMappingBuilder()
                    .add("name", pattern.getName())
                    .add("before", pattern.getStats().getActionBefore().toString())
                    .add("precondition", YamlUtil.collectionToYAMLSeq(
                            pattern.getStats().getPreconditions().stream()
                                    .map(Expression::toString)
                                    .collect(Collectors.toList())))
                    .build());
        }
        ymb = ymb.add("shadowed", sSeqBuilder.build());
        return ymb.build();
    }
}
