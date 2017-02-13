package com.touwolf.plugin.idea.depschecker.ui;

import com.touwolf.plugin.idea.depschecker.model.DependencyInfo;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.Collections;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

public class CheckVersionCellRenderer extends DefaultTableCellRenderer
{
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean hasFocus, int row, int column)
    {
        if (value instanceof DependencyInfo)
        {
            DependencyInfo info = (DependencyInfo) value;
            switch (column)
            {
                case 0:
                {
                    //groupId:artifactId
                    setText(info.getGroupId() + ":" + info.getArtifactId());
                    setHorizontalAlignment(SwingConstants.LEFT);
                    setFont(modifyFont(TextAttribute.WEIGHT_DEMIBOLD));
                    break;
                }
                case 1:
                {
                    //version
                    setText(info.getVersion());
                    setHorizontalAlignment(SwingConstants.RIGHT);
                    setFont(modifyFont(TextAttribute.WEIGHT_BOLD));
                    break;
                }
                case 2:
                {
                    //latest version
                    setText(info.getLatestVersion());
                    setHorizontalAlignment(SwingConstants.RIGHT);
                    setFont(modifyFont(TextAttribute.WEIGHT_BOLD));
                    break;
                }
                case 3:
                {
                    //upgrade action
                    String text = info.canUpgrade() ? "UPGRADE" : "N/A";
                    setText(text);
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setFont(modifyFont(TextAttribute.WEIGHT_BOLD));
                    break;
                }
                default:
                {
                    //just in case
                    setText("");
                    break;
                }
            }
        }
        if (selected)
        {
            setBackground(table.getSelectionBackground());
        }
        setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
        return this;
    }

    private Font modifyFont(Float weight)
    {
        Map<TextAttribute, Float> attrs = Collections.singletonMap(TextAttribute.WEIGHT, weight);
        return getFont().deriveFont(attrs);
    }
}
