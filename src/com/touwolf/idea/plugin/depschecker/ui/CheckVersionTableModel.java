package com.touwolf.idea.plugin.depschecker.ui;

import com.touwolf.idea.plugin.depschecker.model.PomInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;

public class CheckVersionTableModel extends AbstractTableModel
{
    private static final String[] COLUMNS = {
        "Dependency", "Version", "Last Version", "Upgrade"
    };

    private final List data;

    private final Integer rowCount;

    public CheckVersionTableModel(List<PomInfo> pomInfoList)
    {
        data = new ArrayList<>(pomInfoList.size());
        AtomicInteger count = new AtomicInteger(0);
        pomInfoList.forEach(pomInfo ->
        {
            //header
            data.add(pomInfo.getArtifactId());
            String title = "-- Dependencies management:";
            if (pomInfo.getDependenciesManagement().isEmpty())
            {
                title += " (EMPTY)";
            }
            data.add(title);
            pomInfo.getDependenciesManagement().forEach(data::add);
            title = "-- Dependencies:";
            if (pomInfo.getDependencies().isEmpty())
            {
                title += " (EMPTY)";
            }
            data.add(title);
            pomInfo.getDependencies().forEach(depend
                    encyInfo ->
            {
                String dependencyTitle = "---- " + dependencyInfo.getGroupId() + ":" + dependencyInfo.getArtifactId();
                data.add(Arrays.asList(dependencyTitle, dependencyInfo.getVersion(), "", ""));
            });
            count.addAndGet(3 + pomInfo.getDependenciesManagement().size() + pomInfo.getDependencies().size());
        });
        rowCount = count.get();
    }

    @Override
    public int getRowCount()
    {
        return rowCount;
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
        return data.get(rowIndex).get(columnIndex);
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
}
