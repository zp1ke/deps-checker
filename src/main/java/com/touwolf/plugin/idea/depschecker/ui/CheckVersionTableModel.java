package com.touwolf.plugin.idea.depschecker.ui;

import com.touwolf.plugin.idea.depschecker.model.DependencyInfo;
import com.touwolf.plugin.idea.depschecker.model.PomInfo;
import java.util.LinkedList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import org.jetbrains.annotations.Nullable;

public class CheckVersionTableModel extends AbstractTableModel
{
    private static final String[] COLUMNS = {
        "Dependency", "Version", "Last Version", "Upgrade"
    };

    private final List<DependencyInfo> data;

    @SuppressWarnings("unchecked")
    public CheckVersionTableModel(List<PomInfo> pomInfoList)
    {
        data = new LinkedList<>();
        pomInfoList.forEach(pomInfo ->
        {
            pomInfo.getDependenciesManagement()
                .stream()
                .filter(dependencyInfo -> !data.contains(dependencyInfo))
                .forEach(data::add);
            pomInfo.getDependencies()
                .stream()
                .filter(dependencyInfo -> !data.contains(dependencyInfo))
                .forEach(data::add);
        });
        data.sort((dep1, dep2) ->
        {
            int result = dep2.getGroupId().compareTo(dep1.getGroupId());
            if (result == 0)
            {
                result = dep2.getArtifactId().compareTo(dep1.getArtifactId());
            }
            return result;
        });
    }

    @Override
    public int getRowCount()
    {
        return data.size();
    }

    @Override
    public int getColumnCount()
    {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int columnIndex)
    {
        if (columnIndex < 0 || columnIndex >= COLUMNS.length)
        {
            return "";
        }
        return COLUMNS[columnIndex];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        if (columnIndex < 0 || columnIndex >= COLUMNS.length ||
            rowIndex < 0 || rowIndex >= data.size())
        {
            return null;
        }
        return data.get(rowIndex);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
        if (columnIndex >= 0 && columnIndex < COLUMNS.length - 1)
        {
            return String.class;
        }
        if (columnIndex == COLUMNS.length - 1)
        {
            return JButton.class;
        }
        return super.getColumnClass(columnIndex);
    }

    @Nullable
    public DependencyInfo getDependencyInfo(int index)
    {
        if (index >= 0 && index < data.size())
        {
            return data.get(index);
        }
        return null;
    }
}
