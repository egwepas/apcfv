/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.egwepas.apcfv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author egwepas
 */
public class Parser {

    public Map<String, ProfilerTree> forwardTrees = new HashMap<>();
    public Map<String, ProfilerTree> backwardTrees = new HashMap<>();
    public Map<String, ProfilerTree> backwardMethods = new HashMap<>();
    public Map<String, ProfilerMethods> methodsMap = new HashMap<>();

    public Parser(File f) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(f))))) {
            System.out.println("Start parsing file " + f);
            parse(br);
            System.out.println("Finished parsing file " + f);
        } catch (Exception e) {
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                System.out.println("Start parsing file " + f);
                parse(br);
                System.out.println("Finished parsing file " + f);
            }
        }
    }

    public void parse(BufferedReader br) throws IOException {
        String line;
        while (null != (line = br.readLine())) {
            int stackStart;
            String thread;
            if (line.startsWith("[")) {
                stackStart = line.indexOf(']') + 2;
                thread = line.substring(1, stackStart - 2);
            } else {
                thread = "default";
                stackStart = 0;
            }
            try {
                int countIndex = line.lastIndexOf(' ');
                int count = Integer.valueOf(line.substring(countIndex + 1));
                String stackLine = line.substring(stackStart, countIndex);
                String[] stack = stackLine.split(Pattern.quote(";"));
                for(int i=0; i<stack.length; i++){
                    stack[i] = stack[i].intern();
                }
                List<String> methods = Arrays.asList(stack);
                

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
                System.err.println("Error handling line : " + line);
                e.printStackTrace();
            }
        }
    }
}
