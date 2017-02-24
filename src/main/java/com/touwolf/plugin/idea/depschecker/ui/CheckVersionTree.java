package com.touwolf.plugin.idea.depschecker.ui;

import com.intellij.openapi.vfs.VirtualFile;
import com.touwolf.plugin.idea.depschecker.helper.MavenHelper;
import com.touwolf.plugin.idea.depschecker.model.DependencyInfo;
import com.touwolf.plugin.idea.depschecker.model.PomInfo;
import java.util.Collection;
import java.util.List;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

public class CheckVersionTree
{
    private JPanel panel;

    private JToolBar toolbar;

    private JButton upgrade;

    private JTree tree;

    public JPanel getPanel()
    {
        return panel;
    }

    public void updateUI(VirtualFile baseDir)
    {
        //todo: set status initializing
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Projects");
        DefaultTreeModel model = new DefaultTreeModel(root);
        tree.setModel(model);
        SwingUtilities.invokeLater(() ->
        {
            //todo: set status loading
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
        tree.setCellRenderer(new CheckVersionCellRenderer());
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        pomInfos.forEach(pomInfo ->
        {
            CheckVersionTreeNode pomNode = createPomNode(pomInfo);
            root.add(pomNode);
            model.reload(root);
        });
        //todo: set status loaded dependencies
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
}
