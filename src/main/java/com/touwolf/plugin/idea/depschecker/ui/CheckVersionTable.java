package com.touwolf.plugin.idea.depschecker.ui;

import com.intellij.ui.table.JBTable;
import com.touwolf.plugin.idea.depschecker.model.DependencyInfo;
import com.touwolf.plugin.idea.depschecker.model.PomInfo;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CheckVersionTable extends JBTable
{
    private SelectionListener listener;

    public CheckVersionTable(@NotNull List<PomInfo> pomInfos)
    {
        super(new CheckVersionTableModel(pomInfos));
        setStriped(true);
        setDefaultRenderer(Object.class, new CheckVersionCellRenderer());
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
