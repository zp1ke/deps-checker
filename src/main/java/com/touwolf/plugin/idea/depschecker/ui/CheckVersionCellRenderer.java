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
            String upgradeVersion = null;
            int upgradable = 0;
            if (node.isPom())
            {
                setIcon(Icons.MAVEN);
                upgradable = node.upgradableDependencies();
            }
            else if (node.isGradle())
            {
                setIcon(Icons.GRADLE);
                upgradable = node.upgradableDependencies();
            }
            else if (node.isDependency())
            {
                setIcon(PlatformIcons.LIBRARY_ICON);
                upgradeVersion = node.getToUpgradeVersion();
            }
            if (!node.getVersion().isEmpty())
            {
                append("  (" + node.getVersion() + ")", SimpleTextAttributes.GRAY_ATTRIBUTES);
            }
            if (upgradable > 0)
            {
                StringBuilder upgradableText = new StringBuilder()
                    .append(upgradable)
                    .append(upgradable == 1 ? " dependency" : " dependencies")
                    .append(" can upgrade!");
                setToolTipText(upgradableText.toString());
                append("  " + upgradableText.toString(), SimpleTextAttributes.ERROR_ATTRIBUTES);
            }
            if (upgradeVersion != null)
            {
                setToolTipText("Can upgrade to " + upgradeVersion);
                append("  Can upgrade to " + upgradeVersion, SimpleTextAttributes.ERROR_ATTRIBUTES);
            }
        }
    }
}
