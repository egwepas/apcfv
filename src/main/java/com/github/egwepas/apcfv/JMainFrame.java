/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.egwepas.apcfv;

import java.awt.Component;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultTreeModel;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import org.apache.commons.text.StringEscapeUtils;
import javax.swing.event.DocumentListener;

/**
 *
 * @author egwepas
 */
public class JMainFrame extends javax.swing.JFrame {

    public static double FILTER_THRESHOLD = 0.0002;

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        IconFontSwing.register(FontAwesome.getIconFont());

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new JMainFrame(args).setVisible(true);
                } catch (Exception e) {
                    throw new RuntimeException("Forward as RuntimeException", e);
                }
            }
        });
    }

    private ParserJfr profilerOutput;
    static private final String template = "<html><b>%.2f</b>%% - <span style='color:;'>%s</span><b>%s</b>";

    private DefaultListModel<ProfilerMethod> methodsModel;

    private ListCellRenderer methodsRenderer = new DefaultListCellRenderer() {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            ProfilerMethod method = (ProfilerMethod) value;
            String name = method.name.replace('/', '.');
            int offset = 0;
            for (offset = 0; offset < name.length(); offset++) {
                char c = name.charAt(offset);
                if (!('.' == c || Character.isDigit(c) || Character.isAlphabetic(c) && Character.isLowerCase(c))) {
                    break;
                }
            }
            String packag = StringEscapeUtils.escapeHtml4(name.substring(0, offset));
            String simpleName = StringEscapeUtils.escapeHtml4(name.substring(offset));
            String text = String.format(template, method.percentageOfTotal(), packag, simpleName);
            return super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
        }
    };

    /**
     * Creates new form JMainFrame
     */
    public JMainFrame(String[] args) throws IOException {
        initComponents();
        jSplitPane1.setContinuousLayout(true);
        try {
            if (args.length == 0) {
                //Create a file chooser
                final JFileChooser fc = new JFileChooser();
                //In response to a button click:
                int returnVal = fc.showOpenDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    openFile(fc.getSelectedFile());
                } else {
                    JOptionPane.showMessageDialog(this, "No file was selected, exiting.");
                    System.exit(-1);
                }
            } else {
                openFile(new File(args[0]));
            }
        } catch (Exception e) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(os);
            e.printStackTrace(ps);
            JOptionPane.showMessageDialog(this, "Error happenend while openning the file : " + os.toString("UTF8"));
            System.exit(-1);
        }

        this.jTextFieldMethodsFilter.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (null != methodsModel) {
                    String filter = jTextFieldMethodsFilter.getText();
                    if (!filter.isEmpty()) {
                        DefaultListModel<ProfilerMethod> filteredMethodsModel = new DefaultListModel<>();
                        for (int i = 0; i < methodsModel.getSize(); i++) {
                            if (methodsModel.get(i).name.toLowerCase().contains(filter.toLowerCase())) {
                                filteredMethodsModel.addElement(methodsModel.get(i));
                            }
                        }
                        jMethodsList.setModel(filteredMethodsModel);
                    } else {
                        jMethodsList.setModel(methodsModel);
                    }
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel4 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        jThreadsList = new javax.swing.JList<>();
        jPanel1 = new javax.swing.JPanel();
        jTextFieldMethodsFilter = new javax.swing.JTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        jMethodsList = new javax.swing.JList<>();
        jLabel1 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        forwardCallsTreePanel = new com.github.egwepas.apcfv.CallTreePanel();
        backwardCallsTreePanel = new com.github.egwepas.apcfv.CallTreePanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("profile-viewer");

        jSplitPane1.setDividerLocation(200);
        jSplitPane1.setDividerSize(12);
        jSplitPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jScrollPane3.setBorder(null);

        jThreadsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jThreadsList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jThreadsListValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(jThreadsList);

        jTabbedPane1.addTab("Threads", jScrollPane3);

        jPanel1.setBackground(javax.swing.UIManager.getDefaults().getColor("TabbedPane.highlight"));

        jTextFieldMethodsFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldMethodsFilterKeyTyped(evt);
            }
        });

        jMethodsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jMethodsList.setCellRenderer(methodsRenderer);
        jScrollPane4.setViewportView(jMethodsList);

        jLabel1.setText("Filter :");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldMethodsFilter)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldMethodsFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Methods", jPanel1);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );

        jSplitPane1.setLeftComponent(jPanel4);

        forwardCallsTreePanel.setBackground(javax.swing.UIManager.getDefaults().getColor("TabbedPane.highlight"));
        forwardCallsTreePanel.setName("forward"); // NOI18N
        jTabbedPane2.addTab("Forward calls", forwardCallsTreePanel);

        backwardCallsTreePanel.setBackground(javax.swing.UIManager.getDefaults().getColor("TabbedPane.highlight"));
        jTabbedPane2.addTab("Backward calls", backwardCallsTreePanel);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane2)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane2)
                .addContainerGap())
        );

        jSplitPane1.setRightComponent(jPanel5);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void openFile(File file) throws IOException {
        setTitle(getTitle() + " - " + file);
        profilerOutput = new ParserJfr(file);
        List<String> threads = new ArrayList<>(profilerOutput.forwardTrees.keySet());
        Collections.sort(threads, (a, b) -> (a == null || b == null) ? 0 : a.compareTo(b));

        DefaultListModel<String> model = new DefaultListModel<>();
        for (String thread : threads) {
            model.addElement(thread);
        }

        jThreadsList.setModel(model);
        jThreadsList.setSelectedIndex(0);
    }

    private void jThreadsListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jThreadsListValueChanged

        /*new Thread(new Runnable() {
            public void run() {*/
        String thread = jThreadsList.getSelectedValue();

        forwardCallsTreePanel.setModel(new DefaultTreeModel(profilerOutput.forwardTrees.get(thread).toTreeNode()), false);
        backwardCallsTreePanel.setModel(new DefaultTreeModel(profilerOutput.backwardTrees.get(thread).toTreeNode()), true);

        methodsModel = new DefaultListModel<>();
        List<ProfilerMethod> methods = profilerOutput.methodsMap.get(thread).getMethods();
        Collections.sort(methods, (a, b) -> -Math.round(Math.signum(a.percentageOfTotal() - b.percentageOfTotal())));
        for (ProfilerMethod method : methods) {
            if (method.percentageOfTotal() > FILTER_THRESHOLD * 100) {
                methodsModel.addElement(method);
            }
        }
        jMethodsList.setModel(methodsModel);
        //    }
        //}).start();

    }//GEN-LAST:event_jThreadsListValueChanged

    private void jTextFieldMethodsFilterKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldMethodsFilterKeyTyped
    }//GEN-LAST:event_jTextFieldMethodsFilterKeyTyped


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.github.egwepas.apcfv.CallTreePanel backwardCallsTreePanel;
    private com.github.egwepas.apcfv.CallTreePanel forwardCallsTreePanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JList<ProfilerMethod> jMethodsList;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTextField jTextFieldMethodsFilter;
    private javax.swing.JList<String> jThreadsList;
    // End of variables declaration//GEN-END:variables
}
