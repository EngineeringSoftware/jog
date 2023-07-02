package jog.codegen;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import jog.Constants;
import jog.util.JPUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class PatternFile {

    private final String srcPath;
    private String name;
    private StringJoiner outputCode;
    private ClassOrInterfaceDeclaration clz;
    private List<PatternV> patterns;
    private Map<String, PatternV> patternsByName;

    public PatternFile(String srcPath) {
        this.srcPath = srcPath;
        init();
    }

    public String translate() {
        gen();
        return outputCode.toString();
    }

    public String getName() {
        return name;
    }

    public ClassOrInterfaceDeclaration getClz() {
        return clz;
    }

    public List<PatternV> getPatterns() {
        return patterns;
    }

    public PatternV getPattern(String name) {
        if (!patternsByName.containsKey(name)) {
            throw new IllegalArgumentException("This pattern does not exist: " + name);
        }
        return patternsByName.get(name);
    }

    private void init() {
        String filename = Paths.get(srcPath).toFile().getName();
        name = filename.substring(0, filename.lastIndexOf(".java"));

        Path path = Paths.get(srcPath);
        CompilationUnit cu = parseCuFromSource(path);
        String className = path.getFileName().toString();
        className = className.substring(0, className.length() - 5); // remove ".java"
        clz = cu.getClassByName(className).get();
        outputCode = new StringJoiner("\n");
        patterns = clz.getMethods().stream()
                .filter(m -> m.getAnnotationByName(Constants.PATTERN_ANNOT).isPresent())
                .map(PatternV::new).collect(Collectors.toList());
        patternsByName = new HashMap<>();
        for (PatternV p : patterns) {
            patternsByName.put(p.getName(), p);
        }
        for (PatternV p : patterns) {
            p.makeStats(patternsByName);
        }
    }

    private void gen() {
        for (MethodDeclaration md : clz.getMethods()) {
            if (md.getAnnotationByName(Constants.GROUP_ANNOT).isPresent()) {
                GroupV group = new GroupV(md, patterns);
                group.translate();
                String code = group.getTranslation();
                outputCode.add(code);
            }
        }
    }

    /**
     * Parses class from source.
     */
    private static CompilationUnit parseCuFromSource(Path srcPath) {
        try {
            ParseResult<CompilationUnit> result = JPUtil.createJavaParser().parse(srcPath);
            if (result.isSuccessful() && result.getResult().isPresent()) {
                return result.getResult().get();
            } else {
                throw new RuntimeException("Parsing failed: " + srcPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("File reading failed: " + srcPath);
        }
    }
}
