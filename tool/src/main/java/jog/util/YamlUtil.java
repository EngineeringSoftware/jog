package jog.util;

import com.amihaiemil.eoyaml.Scalar;
import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlNode;
import com.amihaiemil.eoyaml.YamlPrinter;
import com.amihaiemil.eoyaml.YamlSequence;
import com.amihaiemil.eoyaml.YamlSequenceBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

public class YamlUtil {
    public static void writeYamlNodeToFile(YamlNode yamlNode, String filename) {
        try {
            final YamlPrinter printer = Yaml.createYamlPrinter(new FileWriter(filename));
            printer.print(yamlNode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static YamlSequence collectionToYAMLSeq(Collection<String> sequence) {
        YamlSequenceBuilder sequenceBuilder = Yaml.createYamlSequenceBuilder();
        for (String e : sequence) {
            sequenceBuilder = sequenceBuilder.add(e);
        }
        return sequenceBuilder.build();
    }

    public static Scalar multilineToYamlLiteralScalar(String multilineText) {
        return Yaml.createYamlScalarBuilder()
                .addLine(multilineText)
                .buildLiteralBlockScalar();
    }
}
