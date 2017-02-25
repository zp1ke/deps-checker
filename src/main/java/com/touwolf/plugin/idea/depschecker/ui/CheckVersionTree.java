package com.touwolf.plugin.idea.depschecker.ui;

import com.intellij.openapi.vfs.VirtualFile;
import com.touwolf.plugin.idea.depschecker.ProjectManager;
import com.touwolf.plugin.idea.depschecker.helper.MavenHelper;
import com.touwolf.plugin.idea.depschecker.model.DependencyInfo;
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

    private JToolBar toolbar;

    private JButton upgrade;

    private JTree tree;

    private JButton reload;

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
        Border outBorder = BorderFactory.createMatteBorder(0, 1, 0, 0, borderColor);
        Border inBorder = BorderFactory.createEmptyBorder(5, 5, 0, 0);
        tree.setBorder(BorderFactory.createCompoundBorder(outBorder, inBorder));
        updateUI("Initializing...");
    }

    private void updateUI(String message)
    {
        setStatus(message, true);
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Projects");
        DefaultTreeModel model = new DefaultTreeModel(root);
        tree.setModel(model);
        upgrading = false;
        SwingUtilities.invokeLater(() ->
        {
            setStatus("Loading projects...", true);
            List<PomInfo> pomInfos = MavenHelper.findPomInfos(baseDir);
            updateTree(model, root, pomInfos);
        });
    }

    private void updateTree(DefaultTreeModel model, DefaultMutableTreeNode root, List<PomInfo> pomInfos)
    {
        if (!SwingUtilities.isEventDispatchThread())
        {
            SwingUtilities.invokeLater(() -> updateTree(model, root, pomInfos));
            return;
        }
        pomInfos.forEach(pomInfo ->
        {
            CheckVersionTreeNode pomNode = createPomNode(pomInfo);
            root.add(pomNode);
            model.reload(root);
        });
        setStatus("Loaded dependencies.", false);
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
                setStatus("Upgrading " + groupArtifact + "...", true);
                manager.upgrade(selectedDependency, dependencyInfo -> updateUI("Upgraded " + groupArtifact + ". Reloading..."));
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

    private void setStatus(String message, boolean loadIndicator)
    {
        //todo
    }

    public void setManager(ProjectManager manager)
    {
        this.manager = manager;
    }
}
