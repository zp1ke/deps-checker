package com.touwolf.plugin.idea.depschecker.ui;

import com.intellij.ide.util.treeView.NodeRenderer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.util.PlatformIcons;
import javax.swing.*;

public class CheckVersionCellRenderer extends NodeRenderer
{
    @Override
    public void customizeCellRenderer(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
        super.customizeCellRenderer(tree, value, selected, expanded, leaf, row, hasFocus);
        setIcon(PlatformIcons.FOLDER_ICON);
        if (CheckVersionTreeNode.class.isAssignableFrom(value.getClass()))
        {
            CheckVersionTreeNode node = (CheckVersionTreeNode) value;
            String appendText = "  (" + node.getVersion() + ")";
            SimpleTextAttributes textAttr = SimpleTextAttributes.GRAY_ATTRIBUTES;
            if (node.isPom())
            {
                setIcon(Icons.MAVEN);
            }
            else if (node.isDependency())
            {
                setIcon(Icons.DEPENDENCY);
                String upgradeVersion = node.getToUpgradeVersion();
                if (upgradeVersion != null)
                {
                    appendText = "  (" + upgradeVersion + ")";
                    textAttr = SimpleTextAttributes.ERROR_ATTRIBUTES;
                }
            }
            append(appendText, textAttr);
        }
    }
}
