/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.egwepas.apcfv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author egwepas
 */
public class ProfilerMethods {

    public int total = 0;
    public Map<String, ProfilerMethod> methods = new HashMap<>();

    public void add(List<String> stack, int count) {
        total += count;
        stack.stream().distinct().forEach(m -> {
            if (methods.containsKey(m)) {
                methods.get(m).count += count;
            } else {
                methods.put(m, new ProfilerMethod(this, m, count));
            }
        });
    }

    public List<ProfilerMethod> getMethods() {
        List<ProfilerMethod> ret = new ArrayList<>(methods.values());
        Collections.sort(ret, (a, b) -> b.count - a.count);
        return ret;
    }
}
