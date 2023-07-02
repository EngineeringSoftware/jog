package jog.stats;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlNode;
import com.amihaiemil.eoyaml.YamlSequenceBuilder;
import jog.util.YamlUtil;

import java.util.List;

public abstract class Writer<T> {

    protected final List<T> data;

    @SafeVarargs
    public Writer(T... data) {
        this.data = List.of(data);
    }

    public Writer(List<T> data) {
        this.data = data;
    }

    public void output(String filename) {
        YamlUtil.writeYamlNodeToFile(toYAML(), filename);
    }

    public YamlNode toYAML() {
        YamlSequenceBuilder seqBuilder = Yaml.createYamlSequenceBuilder();
        for (T i : data) {
            seqBuilder = seqBuilder.add(toYAML(i));
        }
        return seqBuilder.build();
    }

    protected abstract YamlNode toYAML(T item);
}
