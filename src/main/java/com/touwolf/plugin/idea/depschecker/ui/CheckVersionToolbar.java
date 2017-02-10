package com.touwolf.plugin.idea.depschecker.ui;

import com.touwolf.plugin.idea.depschecker.model.DependencyInfo;
import javax.swing.*;
import org.jetbrains.annotations.Nullable;

public class CheckVersionToolbar extends JToolBar implements CheckVersionTable.SelectionListener
{
    public CheckVersionToolbar()
    {
        super(SwingConstants.VERTICAL);
    }

    @Override
    public void selectionChange(@Nullable DependencyInfo dependencyInfo)
    {
        //todo
    }
}
