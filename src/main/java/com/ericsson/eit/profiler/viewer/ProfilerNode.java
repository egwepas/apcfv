/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.eit.profiler.viewer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 *
 * @author egwepas
 */
public class ProfilerNode {

    private int count;
    private ProfilerTree tree;
    private ProfilerNode root;
    private String name;
    private Map<String, ProfilerNode> children = null;

    public ProfilerNode(ProfilerTree tree, ProfilerNode root, String name) {
        this(tree, root, name, 0);
    }

    public ProfilerNode(ProfilerTree tree, ProfilerNode root, String name, int count) {
        this.root = root;
        this.tree = tree;
        this.name = name;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void incCount(int value) {
        this.count += value;
    }

    public ProfilerNode getRoot() {
        return root;
    }

    public String getName() {
        return name;
    }

    public void add(Iterator<String> methods, int count) {
        this.count += count;
        if (methods.hasNext()) {
            if (null == children) {
                children = new HashMap<>();
            }
            String method = methods.next();

            if (children.containsKey(method)) {
                children.get(method).add(methods, count);
            } else {
                ProfilerNode child = new ProfilerNode(tree, null != root ? root : this, method);
                children.put(method, child);
                child.add(methods, count);
            }
        }
    }

    public DefaultMutableTreeNode toTreeNode() {
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(this);
        if (null != children) {
            int childrenCount = 0;
            ArrayList<ProfilerNode> list = new ArrayList<>(children.values());
            Collections.sort(list, (a, b) -> -(a.getCount() - b.getCount()));
            for (ProfilerNode profilerNode : list) {
                childrenCount += profilerNode.getCount();
                treeNode.add(profilerNode.toTreeNode());
            }
            if (childrenCount < this.count) {
                ProfilerNode self = new ProfilerNode(tree, null != root ? root : this, "self", this.count - childrenCount);
                treeNode.insert(new DefaultMutableTreeNode(self), 0);
            }
        }
        return treeNode;
    }

    public float percentageOfTotal() {
        return (count * 100) / (float) tree.getCount();
    }

    public float percentageOf(int total) {
        return (count * 100) / (float) total;
    }

    public Map<String, ProfilerNode> getMethods() {
        Map<String, ProfilerNode> res = new HashMap<>();
        res.put(name, new ProfilerNode(tree, null, name, count));
        if (null != children) {
            for (ProfilerNode child : children.values()) {
                for (ProfilerNode method : child.getMethods().values()) {
                    if (res.containsKey(method.getName())) {
                        res.get(method.getName()).count += method.count;
                    } else {
                        res.put(method.name, new ProfilerNode(tree, null, method.name, method.count));
                    }
                }
            }
        }
        return res;
    }
}
