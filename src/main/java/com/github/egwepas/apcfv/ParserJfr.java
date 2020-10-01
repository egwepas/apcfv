/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.egwepas.apcfv;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import one.jfr.ClassRef;
import one.jfr.Frame;
import one.jfr.JfrReader;
import one.jfr.MethodRef;
import one.jfr.Sample;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author egwepas
 */
public class ParserJfr {

    public Map<String, ProfilerTree> forwardTrees = new HashMap<>();
    public Map<String, ProfilerTree> backwardTrees = new HashMap<>();
    public Map<String, ProfilerTree> backwardMethods = new HashMap<>();
    public Map<String, ProfilerMethods> methodsMap = new HashMap<>();
    JfrReader reader;

    public ParserJfr(File f) throws IOException {
        reader = new JfrReader(f.getAbsolutePath());
        parse();
    }

    private void parse() throws IOException {
        Map<Pair<Integer, Integer>, Integer> counts = new HashMap();
        for (Sample sample : reader.samples) {
            counts.compute(Pair.of(sample.tid, sample.stackTraceId), (k, v) -> {
                if (v == null) {
                    return 1;
                } else {
                    return v + 1;
                }
            });
        }
        List<String> methods = new ArrayList<>();
        for (Map.Entry<Pair<Integer, Integer>, Integer> entry : counts.entrySet()) {

            //for (Sample sample : reader.samples) {
            String thread = new String(reader.threads.get(entry.getKey().getLeft()));
            Frame[] frames = reader.stackTraces.get(entry.getKey().getRight());

            try {
                int count = entry.getValue();
                methods.clear();
                for (int i = frames.length - 1; i >= 0; i--) {
                    Frame frame = frames[i];
                    MethodRef methodRef = reader.methods.get(frame.method);
                    ClassRef classRef = reader.classes.get(methodRef.cls);
                    String className = new String(reader.symbols.get(classRef.name));
                    String methodName = new String(reader.symbols.get(methodRef.name));
                    if (!className.isEmpty()) {
                        methods.add(className + "." + methodName);
                    } else {
                        methods.add(methodName);
                    }
                }

                if (!forwardTrees.containsKey(thread)) {
                    forwardTrees.put(thread, new ProfilerTree());
                }
                forwardTrees.get(thread).incCount(count);
                forwardTrees.get(thread).add(methods.iterator(), count);

                Collections.reverse(methods);

                if (!backwardTrees.containsKey(thread)) {
                    backwardTrees.put(thread, new ProfilerTree());
                }
                backwardTrees.get(thread).incCount(count);
                backwardTrees.get(thread).add(methods.iterator(), count);

                if (!backwardMethods.containsKey(thread)) {
                    backwardMethods.put(thread, new ProfilerTree());
                }
                backwardMethods.get(thread).incCount(count);
                for (int i = 0; i < methods.size() - 1; i++) {
                    backwardMethods.get(thread).add(methods.subList(i, methods.size() - 1).iterator(), count);
                }

                if (!methodsMap.containsKey(thread)) {
                    methodsMap.put(thread, new ProfilerMethods());
                }

                methodsMap.get(thread).add(methods, count);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
