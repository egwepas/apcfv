/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.egwepas.apcfv;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import org.apache.commons.text.StringEscapeUtils;

/**
 *
 * @author egwepas
 */
class CallTreeRenderer extends DefaultTreeCellRenderer {

    //private static final String template = "<html><b>%.2f</b>%% - <span style='color:;'>%s</span><b>%s</b>";
    private static final String template = "%.2f%% - %s%s";

    public CallTreeRenderer(boolean reversed) {
        Color grey = new Color(123, 144, 149);
        setLeafIcon(IconFontSwing.buildIcon(FontAwesome.SQUARE, 16, grey));
        if (reversed) {
            Color orange = new Color(240, 94, 35);
            setOpenIcon(IconFontSwing.buildIcon(FontAwesome.ARROW_LEFT, 16, orange));
            setClosedIcon(IconFontSwing.buildIcon(FontAwesome.ARROW_LEFT, 16, orange));
        } else {
            Color green = new Color(80, 220, 100);
            setOpenIcon(IconFontSwing.buildIcon(FontAwesome.ARROW_RIGHT, 16, green));
            setClosedIcon(IconFontSwing.buildIcon(FontAwesome.ARROW_RIGHT, 16, green));
        }
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) value;
        if (treeNode.getUserObject() instanceof ProfilerTree) {
            setText("root");
        } else if (treeNode.getUserObject() instanceof ProfilerNode) {
            ProfilerNode profilerNode = (ProfilerNode) treeNode.getUserObject();
            
            String name = profilerNode.getName();
            String packagePart = name.substring(0, name.lastIndexOf('/') + 1);
            //packagePart = StringEscapeUtils.escapeHtml4(packagePart.replace('/', '.'));
            //String classPart = StringEscapeUtils.escapeHtml4(name.substring(name.lastIndexOf('/') + 1, name.length()));
            packagePart = packagePart.replace('/', '.');
            String classPart = name.substring(name.lastIndexOf('/') + 1, name.length());
            
            setText(String.format(template, profilerNode.percentageOfTotal(), packagePart, classPart));
        }
        return this;
    }

}
