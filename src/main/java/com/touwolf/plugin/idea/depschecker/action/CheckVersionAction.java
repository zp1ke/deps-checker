package com.touwolf.plugin.idea.depschecker.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import com.touwolf.plugin.idea.depschecker.ProjectManager;
import com.touwolf.plugin.idea.depschecker.helper.MavenHelper;
import com.touwolf.plugin.idea.depschecker.model.DependencyInfo;
import com.touwolf.plugin.idea.depschecker.ui.CheckVersionTree;
import com.touwolf.plugin.idea.depschecker.ui.Icons;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;

public class CheckVersionAction extends AnAction implements ProjectManager
{
    private static final String TOOL_WINDOW_ID = "Check dependencies version";

    private Project project;

    private boolean upgrading = false;

    private CheckVersionTree treeComponent;

    @Override
    public void actionPerformed(AnActionEvent event)
    {
        project = event.getProject();
        if (project == null)
        {
            return;
        }
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
        toolWindow.setIcon(Icons.LOGO);
        ContentManager contentManager = toolWindow.getContentManager();
        contentManager.removeAllContents(true);
        Content content = contentManager.getFactory()
            .createContent(createContent(), "[" + projectName + "]", false);
        contentManager.addContent(content);
    }

    @NotNull
    private JComponent createContent()
    {
        if (treeComponent == null)
        {
            treeComponent = new CheckVersionTree();
        }
        treeComponent.setManager(this);
        treeComponent.updateUI(project.getBaseDir());
        return treeComponent.getPanel();
    }

    @Override
    public void upgrade(@NotNull DependencyInfo dependencyInfo, @NotNull Listener listener)
    {
        if (upgrading)
        {
            return;
        }
        upgrading = true;
        WriteCommandAction.runWriteCommandAction(project, () ->
        {
            MavenHelper.upgradeDependency(project.getBaseDir(), dependencyInfo);
            listener.upgradeDone(dependencyInfo);
            upgrading = false;
        });
    }
}
