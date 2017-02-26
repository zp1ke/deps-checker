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
        setIcon(PlatformIcons.PACKAGE_ICON);
        if (CheckVersionTreeNode.class.isAssignableFrom(value.getClass()))
        {
            CheckVersionTreeNode node = (CheckVersionTreeNode) value;
            String extraAppend = null;
            SimpleTextAttributes textAttr = SimpleTextAttributes.GRAY_ATTRIBUTES;
            if (node.isPom())
            {
                setIcon(Icons.MAVEN);
            }
            else if (node.isGradle())
            {
                setIcon(Icons.GRADLE);
            }
            else if (node.isDependency())
            {
                setIcon(PlatformIcons.LIBRARY_ICON);
                String upgradeVersion = node.getToUpgradeVersion();
                if (upgradeVersion != null)
                {
                    textAttr = SimpleTextAttributes.ERROR_ATTRIBUTES;
                    extraAppend = "Can upgrade to " + upgradeVersion;
                }
            }
            append("  (" + node.getVersion() + ")", textAttr);
            if (extraAppend != null)
            {
                setToolTipText(extraAppend);
                append("  " + extraAppend, SimpleTextAttributes.GRAY_ATTRIBUTES);
            }
        }
    }
}
