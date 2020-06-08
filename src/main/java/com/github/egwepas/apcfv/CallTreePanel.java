/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.egwepas.apcfv;

import java.util.Arrays;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author egwepas
 */
public class CallTreePanel extends javax.swing.JPanel {

    private boolean autoExpandEnabled = true;

    /**
     * Creates new form CallTreePanel
     */
    public CallTreePanel() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTree = new javax.swing.JTree();
        jTextFieldSearch = new javax.swing.JTextField();
        jNextButton = new javax.swing.JButton();
        jPreviousButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setBackground(javax.swing.UIManager.getDefaults().getColor("TabRenderer.selectedActivatedForeground"));

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        jTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jTree.setRootVisible(false);
        jTree.setShowsRootHandles(true);
        jTree.addTreeExpansionListener(new javax.swing.event.TreeExpansionListener() {
            public void treeCollapsed(javax.swing.event.TreeExpansionEvent evt) {
            }
            public void treeExpanded(javax.swing.event.TreeExpansionEvent evt) {
                jTreeTreeExpanded(evt);
            }
        });
        jScrollPane1.setViewportView(jTree);

        jNextButton.setText("Next");
        jNextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jNextButtonActionPerformed(evt);
            }
        });

        jPreviousButton.setText("Previous");
        jPreviousButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPreviousButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("Search :");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldSearch, javax.swing.GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPreviousButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jNextButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jNextButton)
                    .addComponent(jPreviousButton)
                    .addComponent(jTextFieldSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jTreeTreeExpanded(javax.swing.event.TreeExpansionEvent evt) {//GEN-FIRST:event_jTreeTreeExpanded
        if (autoExpandEnabled) {
            JTree tree = (JTree) evt.getSource();
            TreePath path = evt.getPath();
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
            int row = tree.getRowForPath(path);
            if (node.getChildCount() == 1) {
                tree.collapseRow(row + 1);
                tree.expandRow(row + 1);
            } else {
                tree.setSelectionRow(row);
                SwingUtilities.invokeLater(() -> tree.scrollPathToVisible(path));
            }
        }
    }//GEN-LAST:event_jTreeTreeExpanded

    private void jNextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jNextButtonActionPerformed
        searchTreeNodes(true);
    }//GEN-LAST:event_jNextButtonActionPerformed

    private void jPreviousButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPreviousButtonActionPerformed
        searchTreeNodes(false);
    }//GEN-LAST:event_jPreviousButtonActionPerformed

    private void searchTreeNodes(boolean forward) {
        String text = jTextFieldSearch.getText();
        TreePath currentPath = jTree.getSelectionPath();
        if (null == currentPath) {
            jTree.setSelectionRow(0);
            currentPath = jTree.getSelectionPath();
        }
        DefaultMutableTreeNode nextNode;
        if (forward) {
            nextNode = ((DefaultMutableTreeNode) currentPath.getLastPathComponent()).getNextNode();
        } else {
            nextNode = ((DefaultMutableTreeNode) currentPath.getLastPathComponent()).getPreviousNode();
        }

        boolean found = false;

        while (!found) {
            ProfilerNode nextProfilerNode = (ProfilerNode) nextNode.getUserObject();
            if (nextProfilerNode != null) {
                String name = nextProfilerNode.getName().replace('/', '.');
                if (name.toLowerCase().contains(text.toLowerCase())) {
                    found = true;
                } else {
                    if (forward) {
                        nextNode = nextNode.getNextNode();
                    } else {
                        nextNode = nextNode.getPreviousNode();
                    }
                }
            } else {
                break;
            }
            if(null == nextNode){
                break;
            }
        }

        if (found) {
            autoExpandEnabled = false;
            TreePath path = new TreePath(nextNode.getPath());
            jTree.setSelectionPath(path);
            jTree.scrollPathToVisible(path);
            autoExpandEnabled = true;
        }
    }

    public void setModel(DefaultTreeModel model, boolean reversed) {
        jTree.setCellRenderer(new CallTreeRenderer(reversed));
        jTree.setModel(model);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton jNextButton;
    private javax.swing.JButton jPreviousButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextFieldSearch;
    private javax.swing.JTree jTree;
    // End of variables declaration//GEN-END:variables
}