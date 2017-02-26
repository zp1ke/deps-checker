package com.touwolf.plugin.idea.depschecker.ui;

import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.vfs.VirtualFile;
import com.touwolf.plugin.idea.depschecker.ProjectManager;
import com.touwolf.plugin.idea.depschecker.helper.GradleHelper;
import com.touwolf.plugin.idea.depschecker.helper.MavenHelper;
import com.touwolf.plugin.idea.depschecker.model.DependencyInfo;
import com.touwolf.plugin.idea.depschecker.model.GradleInfo;
import com.touwolf.plugin.idea.depschecker.model.PomInfo;
import java.awt.*;
import java.util.Collection;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

public class CheckVersionTree
{
    private JPanel panel;

    @SuppressWarnings("unused")
    private JToolBar toolbar;

    private JButton upgrade;

    private JTree tree;

    private JButton reload;

    private JLabel status;

    private ProjectManager manager;

    private DependencyInfo selectedDependency;

    private VirtualFile baseDir;

    private boolean upgrading;

    public JPanel getPanel()
    {
        return panel;
    }

    public void updateUI(VirtualFile baseDir)
    {
        this.baseDir = baseDir;
        tree.setCellRenderer(new CheckVersionCellRenderer());
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        Color borderColor = new Color(46, 45, 45);
        status.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor));
        Border outBorder = BorderFactory.createMatteBorder(0, 1, 0, 0, borderColor);
        Border inBorder = BorderFactory.createEmptyBorder(5, 5, 0, 0);
        tree.setBorder(BorderFactory.createCompoundBorder(outBorder, inBorder));
        updateUI("Initializing...");
    }

    private void updateUI(String message)
    {
        setStatus(message, 0);
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Projects");
        DefaultTreeModel model = new DefaultTreeModel(root);
        tree.setModel(model);
        upgrading = false;
        SwingUtilities.invokeLater(() ->
        {
            setStatus("Loading projects...", 0);
            List<PomInfo> pomInfos = MavenHelper.findPomInfos(baseDir);
            List<GradleInfo> gradleInfos = GradleHelper.findGradleInfos(baseDir);
            updateTree(model, root, pomInfos, gradleInfos);
        });
    }

    private void updateTree(DefaultTreeModel model, DefaultMutableTreeNode root,
                            List<PomInfo> pomInfos, List<GradleInfo> gradleInfos)
    {
        if (!SwingUtilities.isEventDispatchThread())
        {
            SwingUtilities.invokeLater(() -> updateTree(model, root, pomInfos, gradleInfos));
            return;
        }
        pomInfos.forEach(pomInfo ->
        {
            CheckVersionTreeNode pomNode = createPomNode(pomInfo);
            root.add(pomNode);
            model.reload(root);
        });
        gradleInfos.forEach(gradleInfo ->
        {
            CheckVersionTreeNode gradleNode = createGradleNode(gradleInfo);
            root.add(gradleNode);
            model.reload(root);
        });
        setStatus("Loaded dependencies.", 2000L);
        configListeners();
    }

    private void configListeners()
    {
        tree.getSelectionModel().addTreeSelectionListener(e ->
        {
            if (!upgrading)
            {
                upgrade.setEnabled(false);
                Object node = tree.getLastSelectedPathComponent();
                selectedDependency = null;
                if (node != null && node instanceof CheckVersionTreeNode)
                {
                    CheckVersionTreeNode treeNode = (CheckVersionTreeNode) node;
                    boolean canUpgrade = treeNode.isDependency() && treeNode.getToUpgradeVersion() != null;
                    upgrade.setEnabled(canUpgrade);
                    if (canUpgrade)
                    {
                        selectedDependency = treeNode.getDependency();
                    }
                }
            }
        });
        upgrade.addActionListener(e ->
        {
            upgrade.setEnabled(false);
            upgrading = true;
            if (manager != null && selectedDependency != null)
            {
                String groupArtifact = selectedDependency.getGroupId() + ":" + selectedDependency.getArtifactId();
                setStatus("Upgrading " + groupArtifact + "...", 0);
                manager.upgrade(selectedDependency, dependencyInfo ->
                {
                    String message = "Upgraded " + groupArtifact + ". Reloading...";
                    manager.notifyByBallon(message, MessageType.INFO);
                    updateUI(message);
                });
            }
        });
        reload.addActionListener(e ->
        {
            upgrade.setEnabled(false);
            upgrading = true;
            updateUI("Reloading...");
        });
    }

    private CheckVersionTreeNode createPomNode(PomInfo pomInfo)
    {
        CheckVersionTreeNode pomNode = new CheckVersionTreeNode(pomInfo);
        DefaultMutableTreeNode depsNode = createDependenciesNode(pomInfo.getDependenciesManagement(), "Dependencies Management");
        if (depsNode != null)
        {
            pomNode.add(depsNode);
        }
        depsNode = createDependenciesNode(pomInfo.getDependencies(), "Dependencies");
        if (depsNode != null)
        {
            pomNode.add(depsNode);
        }
        return pomNode;
    }

    private CheckVersionTreeNode createGradleNode(GradleInfo gradleInfo)
    {
        CheckVersionTreeNode gradleNode = new CheckVersionTreeNode(gradleInfo);
        DefaultMutableTreeNode depsNode = createDependenciesNode(gradleInfo.getDependencies(), "Dependencies");
        if (depsNode != null)
        {
            gradleNode.add(depsNode);
        }
        return gradleNode;
    }

    private DefaultMutableTreeNode createDependenciesNode(Collection<DependencyInfo> dependencyInfos, String name)
    {
        if (!dependencyInfos.isEmpty())
        {
            DefaultMutableTreeNode depsNode = new DefaultMutableTreeNode(name);
            dependencyInfos.forEach(dependencyInfo ->
            {
                CheckVersionTreeNode depNode = new CheckVersionTreeNode(dependencyInfo);
                depsNode.add(depNode);
            });
            return depsNode;
        }
        return null;
    }

    private void setStatus(String message, long millis)
    {
        boolean visible = message != null;
        status.setVisible(visible);
        status.setText(visible ? message : "");
        if (millis > 0)
        {
            new Thread(() ->
            {
                try
                {
                    Thread.sleep(millis);
                }
                catch (InterruptedException ignored)
                {
                }
                SwingUtilities.invokeLater(() -> setStatus(null, 0));
            }).start();
        }
    }

    public void setManager(ProjectManager manager)
    {
        this.manager = manager;
    }
}
