/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.egwepas.apcfv;

import static com.github.egwepas.apcfv.JMainFrame.FILTER_THRESHOLD;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author egwepas
 */
public class ProfilerTree {

    private Map<String, ProfilerNode> children = new HashMap<>();
    private int count = 0;

    public int getCount() {
        return count;
    }

    public void incCount(int value) {
        count += value;
    }

    public void add(Iterator<String> methods, int count) {
        if (methods.hasNext()) {
            String method = methods.next();
            if (children.containsKey(method)) {
                children.get(method).add(methods, count);
            } else {
                ProfilerNode child = new ProfilerNode(this, null, method, 0);
                children.put(method, child);
                child.add(methods, count);
            }
        }
    }

    public DefaultMutableTreeNode toTreeNode() {
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(this);
        if (null != children) {
            ArrayList<ProfilerNode> list = new ArrayList<>(children.values());
            Collections.sort(list, (a, b) -> -(a.getCount() - b.getCount()));
            for (ProfilerNode profilerNode : list) {
                if (((1.0 * profilerNode.getCount()) / count) > FILTER_THRESHOLD) {
                    treeNode.add(profilerNode.toTreeNode());
                }
            }
        }
        return treeNode;
    }

    public List<ProfilerNode> getMethods() {
        Map<String, ProfilerNode> res = new HashMap<>();
        for (ProfilerNode child : children.values()) {
            for (ProfilerNode method : child.getMethods().values()) {
                if (res.containsKey(method.getName())) {
                    res.get(method.getName()).incCount(method.getCount());
                } else {
                    res.put(method.getName(), new ProfilerNode(this, null, method.getName(), method.getCount()));
                }
            }
        }
        List<ProfilerNode> ret = new ArrayList<ProfilerNode>(res.values());
        Collections.sort(ret, (a, b) -> -(a.getCount() - b.getCount()));
        return ret;
    }
}
