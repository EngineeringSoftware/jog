package jog.stats;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlNode;
import jog.util.YamlUtil;

import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class StatsWriter extends Writer<Stats> {

    public StatsWriter(Stats... statses) {
        super(statses);
    }

    public StatsWriter(List<Stats> statses) {
        super(statses);
    }

    @Override
    protected YamlNode toYAML(Stats stats) {
        return Yaml.createYamlMappingBuilder()
                .add("name", stats.getName())
                .add("before", stats.getActionBefore().toString())
                .add("after", stats.getActionAfter().toString())
                .add("origin", stats.getOrigin())
                .add("type", stats.getNodeType().toCodeGen(stats.getValType()))
                // sort by pattern name
                .add("shadows", YamlUtil.collectionToYAMLSeq(new TreeSet<>(stats.getShadows().stream().map(p -> p.getStats().getName()).collect(Collectors.toSet()))))
                // sort by pattern name
                .add("composites", YamlUtil.collectionToYAMLSeq(new TreeSet<>(stats.getComposites().stream().map(p -> p.getStats().getName()).collect(Collectors.toSet()))))
                .add("pr", Integer.toString(stats.getPr()))
                .add("pattern", YamlUtil.multilineToYamlLiteralScalar(stats.getPatternCode()))
                .add("generated", YamlUtil.multilineToYamlLiteralScalar(stats.getGeneratedCode()))
                .build();
    }

}
