package com.touwolf.plugin.idea.depschecker.ui;

import com.intellij.openapi.vfs.VirtualFile;
import com.touwolf.plugin.idea.depschecker.helper.MavenHelper;
import com.touwolf.plugin.idea.depschecker.model.PomInfo;
import java.util.List;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

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
        pomInfos.forEach(pomInfo ->
        {
            DefaultMutableTreeNode pomNode = new DefaultMutableTreeNode(pomInfo.getArtifactId());
            root.add(pomNode);
            model.reload(root);
        });
        //todo: set status loaded dependencies
    }
}
