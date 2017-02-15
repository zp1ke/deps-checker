package com.touwolf.plugin.idea.depschecker.ui;

import com.touwolf.plugin.idea.depschecker.ProjectManager;
import com.touwolf.plugin.idea.depschecker.model.DependencyInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CheckVersionToolbar extends JToolBar implements CheckVersionTable.SelectionListener, ActionListener
{
    private final ProjectManager manager;

    private DependencyInfo dependencyInfo;

    private JButton upgradeButton;

    private boolean upgrading = false;

    public CheckVersionToolbar(@NotNull ProjectManager manager)
    {
        super(SwingConstants.VERTICAL);
        this.manager = manager;
        setFloatable(false);
        setRollover(true);
        upgradeButton = new JButton("UPGRADE");
        upgradeButton.addActionListener(this);
        upgradeButton.setEnabled(false);
        add(upgradeButton);
    }

    @Override
    public void selectionChange(@Nullable DependencyInfo dependencyInfo)
    {
        this.dependencyInfo = dependencyInfo;
        upgradeButton.setEnabled(dependencyInfo != null && dependencyInfo.canUpgrade());
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (dependencyInfo != null && dependencyInfo.canUpgrade() && !upgrading)
        {
            upgrading = true;
            upgradeButton.setEnabled(false);
            manager.upgrade(dependencyInfo);
            upgrading = false;
        }
    }
}
