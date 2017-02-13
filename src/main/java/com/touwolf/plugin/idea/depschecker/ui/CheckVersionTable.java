package com.touwolf.plugin.idea.depschecker.ui;

import com.intellij.ui.table.JBTable;
import com.touwolf.plugin.idea.depschecker.model.DependencyInfo;
import com.touwolf.plugin.idea.depschecker.model.PomInfo;
import java.util.List;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CheckVersionTable extends JBTable
{
    private final CheckVersionTableModel model;

    private SelectionListener listener;

    public CheckVersionTable(@NotNull List<PomInfo> pomInfos)
    {
        super();
        model = new CheckVersionTableModel(pomInfos);
        setModel(model);
        setStriped(true);
        setDefaultRenderer(Object.class, new CheckVersionCellRenderer());
        getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        getSelectionModel().addListSelectionListener(e -> selectIndex(e.getFirstIndex()));
    }

    private void selectIndex(int index)
    {
        if (listener != null)
        {
            listener.selectionChange(model.getDependencyInfo(index));
        }
    }

    public void setSelectionListener(@Nullable SelectionListener listener)
    {
        this.listener = listener;
    }

    public interface SelectionListener
    {
        void selectionChange(@Nullable DependencyInfo dependencyInfo);
    }
}
