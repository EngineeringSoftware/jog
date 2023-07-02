package jog.shadow;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlNode;
import com.amihaiemil.eoyaml.YamlSequenceBuilder;
import com.github.javaparser.utils.Pair;

import jog.Constants;
import jog.ast.expr.CGExpr;
import jog.codegen.PatternV;
import jog.log.Log;
import jog.stats.CompositeWriter;
import jog.stats.ShadowWriter;
import jog.util.YamlUtil;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class PatternRelation {

    private final List<PatternV> patterns;
    private final Map<PatternV, Set<PatternV>> shadows; // key's before subsumes value's before
    private final Map<PatternV, Set<PatternV>> unknownShadows; // unknown if shadowing
    private final Map<PatternV, Set<PatternV>> composites; // key's after composites value's before
    private final Map<PatternV, Set<PatternV>> unknownComposites; // key's after composites value's before
    private final List<Seq> compositionSeqs;

    /* Maximum allowed length of a composited sequence of patterns. */
    private final int MAX_LEN = 2;

    public PatternRelation(List<PatternV> patterns) {
        this.patterns = patterns;
        shadows = new HashMap<>();
        unknownShadows = new HashMap<>();
        composites = new HashMap<>();
        unknownComposites = new HashMap<>();
        compositionSeqs = new LinkedList<>();
    }

    public Map<PatternV, Set<PatternV>> getShadows() {
        return shadows;
    }

    public Map<PatternV, Set<PatternV>> getUnknownShadows() {
        return unknownShadows;
    }

    public Map<PatternV, Set<PatternV>> getComposites() {
        return composites;
    }

    public Map<PatternV, Set<PatternV>> getUnknownComposites() {
        return unknownComposites;
    }

    public void analyze() {
        // Analyze for every two patterns (the order matters)
        // Check if pattern1's before subsumes pattern2's before
        // (pattern2 is a special case of pattern1)
        for (PatternV pattern1 : patterns) {
            for (PatternV pattern2 : patterns) {
                if (pattern1 == pattern2) {
                    continue;
                }

                // Shadow
                AstSubsume as = new AstSubsume(
                        pattern1.getBeforeNode(),
                        pattern2.getBeforeNode(),
                        pattern1.getPreconditions(),
                        pattern2.getPreconditions());
                Status as_status = as.check();
                if (as_status == Status.OK) {
                    addToMap(shadows, pattern1, pattern2);
                } else if (as_status == Status.UNKNOWN) {
                    Log.info("Unknown if " + pattern1 + " shadows " + pattern2);
                    addToMap(unknownShadows, pattern1, pattern2);
                }
            }
        }
    }

    public void analyzeComposition() {
        // Analyze composition of every two patterns
        // whether pattern2's before composites pattern1's after
        // (pattern2 can be applied immediately after pattern1)
        for (PatternV pattern1 : patterns) {
            for (PatternV pattern2 : patterns) {
                // Composite
                PatternS x = new PatternS(pattern1);
                PatternS y = new PatternS(pattern2);
                AstComposite ac = AstComposite.makeAstComposite(x, y);
                Status ac_status = ac.check();
                if (ac_status == Status.OK) {
                    addToMap(composites, pattern1, pattern2);
                    ac.composite();
                } else if (ac_status == Status.UNKNOWN) {
                    Log.info("Unknown if " + pattern1 + " composites " + pattern2);
                    addToMap(unknownComposites, pattern1, pattern2);
                }
            }
        }

        // Try to generate all possible composited sequences of
        // patterns up to length MAX_LEN.
        List<Seq> allSeqs = new LinkedList<>(); // all sequences we can use to append other sequences
        Deque<Seq> worklist = new ArrayDeque<>(); // the remaining sequences that can be appended
        for (PatternV patternV : patterns) {
            if (!patternV.getStats().getOrigin().equals(Constants.OPENJDK)) {
                continue;
            }
            PatternS patternS = new PatternS(patternV);
            Seq s = new Seq(patternS, List.of(patternV));
            allSeqs.add(s);
            worklist.addLast(s);
        }
        Set<Pair<Seq, Seq>> visited = new HashSet<>();
        while (!worklist.isEmpty()) {
            Seq next = worklist.removeFirst();
            boolean canBeAppended = false;
            List<Seq> newSeqs = new LinkedList<>();
            for (Seq seq : allSeqs) {
                // If we are compositing a pattern with itself.
                Pair<Seq, Seq> pair = new Pair<>(next, seq);
                if (!visited.add(pair)) {
                    continue;
                }
                AstComposite ac = AstComposite.makeAstComposite(next.pattern, seq.pattern);
                if (next.len + seq.len <= MAX_LEN) {
                    Status ac_status = ac.check();
                    if (ac_status == Status.OK) {
                        canBeAppended = true;
                        List<PatternV> newPatterns = new LinkedList<>(next.patterns);
                        newPatterns.addAll(seq.patterns);
                        PatternS newPattern = ac.composite();
                        Seq newS = new Seq(newPattern, newPatterns);
                        newSeqs.add(newS);
                        if (newS.len < MAX_LEN) {
                            worklist.add(newS);
                        }
                    } else if (ac_status == Status.UNKNOWN) {
                        Log.info("Unknown if " + next.pattern + " composites " + seq.pattern);
                    }
                }
            }
            allSeqs.addAll(newSeqs);
            compositionSeqs.addAll(newSeqs);
            if (canBeAppended) {
                worklist.add(next);
            }
        }
    }

    static class Seq {
        PatternS pattern;
        List<PatternV> patterns;
        int len;

        Seq(PatternS pattern, List<PatternV> patterns) {
            this.pattern = pattern;
            this.patterns = patterns;
            this.len = patterns.size();
        }
    }

    public void output() {
        YamlUtil.writeYamlNodeToFile(mapToYaml(shadows, false), "shadows.yml");
        if (!unknownShadows.isEmpty()) {
            YamlUtil.writeYamlNodeToFile(mapToYaml(unknownShadows, false), "unknown-shadows.yml");
        }
        // YamlUtil.writeYamlNodeToFile(mapToYaml(composites, true), "composites.yml");
        // if (!unknownComposites.isEmpty()) {
        //     YamlUtil.writeYamlNodeToFile(mapToYaml(unknownComposites, true), "unknown-composites.yml");
        // }
        // YamlUtil.writeYamlNodeToFile(seqsToYaml(compositionSeqs), "composition-seqs.yml");
    }

    private YamlNode seqsToYaml(List<Seq> seqs) {
        YamlSequenceBuilder seqBuilder = Yaml.createYamlSequenceBuilder();
        for (Seq s : seqs) {
            YamlMapping mappings = Yaml.createYamlMappingBuilder()
                    .add("before", s.pattern.before.toString())
                    .add("after", s.pattern.after.toString())
                    .add("preconditions", YamlUtil.collectionToYAMLSeq(
                            s.pattern.preconditions.stream().map(CGExpr::toString).collect(Collectors.toList())))
                    .add("sequence", YamlUtil.collectionToYAMLSeq(
                        s.patterns.stream().map(PatternV::getName).collect(Collectors.toList())))
                    .build();
            seqBuilder = seqBuilder.add(mappings);
        }
        return seqBuilder.build();
    }

    private YamlNode mapToYaml(Map<PatternV, Set<PatternV>> map, boolean shadowOrComposite) {
        YamlSequenceBuilder seqBuilder = Yaml.createYamlSequenceBuilder();
        for (PatternV p : patterns) {
            if (!map.containsKey(p)) {
                continue;
            }
            seqBuilder = seqBuilder.add(
                    !shadowOrComposite ?
                            ShadowWriter.toYAML(p.getStats(), map.get(p)) :
                            CompositeWriter.toYAML(p.getStats(), map.get(p)));
        }
        return seqBuilder.build();
    }

    private static void addToMap(Map<PatternV, Set<PatternV>> map, PatternV pattern1, PatternV pattern2) {
        Set<PatternV> set = map.getOrDefault(pattern1, new TreeSet<>());
        set.add(pattern2);
        map.putIfAbsent(pattern1, set);
    }
}
