package com.touwolf.plugin.idea.depschecker.ui;

import com.touwolf.plugin.idea.depschecker.model.DependencyInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import org.jetbrains.annotations.Nullable;

public class CheckVersionToolbar extends JToolBar implements CheckVersionTable.SelectionListener, ActionListener
{
    private DependencyInfo dependencyInfo;

    private JButton upgradeButton;

    public CheckVersionToolbar()
    {
        super(SwingConstants.VERTICAL);
        upgradeButton = new JButton("UPGRADE");
        upgradeButton.addActionListener(this);
        upgradeButton.setEnabled(false);
        add(upgradeButton);
    }

    @Override
    public void selectionChange(@Nullable DependencyInfo dependencyInfo)
    {
        this.dependencyInfo = dependencyInfo;
        upgradeButton.setEnabled(dependencyInfo != null);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (dependencyInfo != null)
        {
            System.out.println("Will upgrade: " + dependencyInfo);
            //todo
        }
    }
}
