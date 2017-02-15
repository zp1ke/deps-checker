package com.touwolf.plugin.idea.depschecker.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import com.touwolf.plugin.idea.depschecker.ProjectManager;
import com.touwolf.plugin.idea.depschecker.helper.MavenHelper;
import com.touwolf.plugin.idea.depschecker.model.DependencyInfo;
import com.touwolf.plugin.idea.depschecker.model.PomInfo;
import com.touwolf.plugin.idea.depschecker.ui.CheckVersionTable;
import com.touwolf.plugin.idea.depschecker.ui.CheckVersionToolbar;
import com.touwolf.plugin.idea.depschecker.ui.Icons;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;

public class CheckVersionAction extends AnAction implements ProjectManager
{
    private static final String TOOL_WINDOW_ID = "Check dependencies version";

    private CheckVersionTable table;

    private List<PomInfo> pomInfos;

    private Project project;

    @Override
    public void actionPerformed(AnActionEvent event)
    {
        project = event.getProject();
        if (project == null)
        {
            return;
        }
        pomInfos = MavenHelper.findPomInfos(project.getBaseDir());
        ToolWindowManager toolWindowMgr = ToolWindowManager.getInstance(project);
        ToolWindow tw = toolWindowMgr.getToolWindow(TOOL_WINDOW_ID);
        if (tw == null)
        {
            tw = toolWindowMgr.registerToolWindow(TOOL_WINDOW_ID, true, ToolWindowAnchor.BOTTOM, true);
        }
        final ToolWindow toolWindow = tw;
        toolWindow.activate(() -> updateContent(toolWindow, project.getName()), true);
    }

    private void updateContent(ToolWindow toolWindow, String projectName)
    {
        toolWindow.setIcon(Icons.DEPS);
        ContentManager contentManager = toolWindow.getContentManager();
        contentManager.removeAllContents(true);
        Content content = contentManager.getFactory()
            .createContent(createContent(), "[" + projectName + "]", false);
        contentManager.addContent(content);
    }

    @NotNull
    private JComponent createContent()
    {
        CheckVersionToolbar toolBar = new CheckVersionToolbar(this);

        table = new CheckVersionTable(pomInfos);
        table.setSelectionListener(toolBar);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(table.getTableHeader(), BorderLayout.PAGE_START);
        tablePanel.add(table, BorderLayout.CENTER);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(toolBar, BorderLayout.WEST);
        panel.add(tablePanel, BorderLayout.CENTER);
        return panel;
    }

    @Override
    public void upgrade(@NotNull DependencyInfo dependencyInfo)
    {
        MavenHelper.upgradeDependency(project.getBaseDir(), dependencyInfo);
        if (table != null)
        {
            pomInfos = MavenHelper.findPomInfos(project.getBaseDir());
            table.update(pomInfos);
        }
    }
}
