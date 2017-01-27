package com.touwolf.plugin.idea.depschecker.ui;

import com.touwolf.plugin.idea.depschecker.model.DependencyInfo;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

public class CheckVersionCellRenderer extends DefaultTableCellRenderer
{
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean hasFocus, int row, int column)
    {
        setText("");
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
                    break;
                }
                case 1:
                {
                    //version
                    setText(info.getVersion());
                    setHorizontalAlignment(SwingConstants.RIGHT);
                    break;
                }
            }
        }
        if (selected)
        {
            setBackground(table.getSelectionBackground());
        }
        if (hasFocus)
        {
            setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
        }
        else
        {
            setBorder(null);
        }
        return this;
    }
}
