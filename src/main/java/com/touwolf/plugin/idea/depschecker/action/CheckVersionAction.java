package com.touwolf.plugin.idea.depschecker.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import com.touwolf.plugin.idea.depschecker.ProjectManager;
import com.touwolf.plugin.idea.depschecker.model.DependencyInfo;
import com.touwolf.plugin.idea.depschecker.model.PomInfo;
import com.touwolf.plugin.idea.depschecker.ui.CheckVersionTable;
import com.touwolf.plugin.idea.depschecker.ui.CheckVersionToolbar;
import com.touwolf.plugin.idea.depschecker.ui.Icons;
import java.awt.*;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.swing.*;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        pomInfos = findPomInfos(project.getBaseDir());
        ToolWindowManager toolWindowMgr = ToolWindowManager.getInstance(project);
        ToolWindow tw = toolWindowMgr.getToolWindow(TOOL_WINDOW_ID);
        if (tw == null)
        {
            tw = toolWindowMgr.registerToolWindow(TOOL_WINDOW_ID, true, ToolWindowAnchor.BOTTOM, true);
        }
        final ToolWindow toolWindow = tw;
        toolWindow.activate(() -> updateContent(toolWindow, project.getName()), true);
    }

    @NotNull
    private List<PomInfo> findPomInfos(@NotNull VirtualFile baseDir)
    {
        List<PomInfo> poms = new LinkedList<>();
        if (!baseDir.isDirectory() || baseDir.getName().startsWith("."))
        {
            return poms;
        }
        VirtualFile[] children = baseDir.getChildren();
        for (VirtualFile child : children)
        {
            if (child.isDirectory())
            {
                poms.addAll(findPomInfos(child));
            }
            else
            {
                PomInfo pom = parsePom(child);
                if (pom != null)
                {
                    poms.add(pom);
                }
            }
        }
        return poms;
    }

    @Nullable
    private PomInfo parsePom(@NotNull VirtualFile file)
    {
        if (file.isDirectory() || !"pom.xml".equals(file.getName()))
        {
            return null;
        }

        MavenXpp3Reader reader = new MavenXpp3Reader();
        try
        {
            Model model = reader.read(file.getInputStream());
            Model parent = null;
            if (model.getParent() != null && file.getParent() != null && file.getParent().getParent() != null)
            {
                VirtualFile parentDirFile = file.getParent().getParent();
                VirtualFile parentFile = parentDirFile.findChild("pom.xml");
                if (parentFile != null)
                {
                    parent = reader.read(parentFile.getInputStream());
                }
            }
            return PomInfo.parse(model, parent);
        }
        catch (IOException | XmlPullParserException e)
        {
            return null;
        }
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
        //todo
        if (table != null)
        {
            pomInfos = findPomInfos(project.getBaseDir());
            table.update(pomInfos);
        }
    }
}
