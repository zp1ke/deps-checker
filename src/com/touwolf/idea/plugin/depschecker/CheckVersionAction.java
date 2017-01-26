package com.touwolf.idea.plugin.depschecker;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import com.intellij.util.ui.JBUI;
import com.touwolf.idea.plugin.depschecker.model.PomInfo;
import java.awt.*;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.swing.*;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class CheckVersionAction extends AnAction
{
    private static final String TOOL_WINDOW_ID = "check_version_action_tool_window_id";

    @Override
    public void actionPerformed(AnActionEvent event)
    {
        Project project = event.getProject();
        if (project == null)
        {
            return;
        }
        List<PomInfo> pomInfos = findPomInfos(project.getBaseDir());
        ToolWindowManager toolWindowMgr = ToolWindowManager.getInstance(project);
        ToolWindow tw = toolWindowMgr.getToolWindow(TOOL_WINDOW_ID);
        if (tw == null)
        {
            tw = toolWindowMgr.registerToolWindow(TOOL_WINDOW_ID, true, ToolWindowAnchor.BOTTOM, true);
        }
        final ToolWindow toolWindow = tw;
        toolWindow.activate(() -> updateContent(toolWindow, pomInfos), true);
    }

    private List<PomInfo> findPomInfos(VirtualFile baseDir)
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

    private PomInfo parsePom(VirtualFile file)
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

    private void updateContent(ToolWindow toolWindow, List<PomInfo> pomInfos)
    {
        ContentManager contentManager = toolWindow.getContentManager();
        contentManager.removeAllContents(true);
        Content content = contentManager.getFactory().createContent(createContent(pomInfos), "TITLE", false);
        contentManager.addContent(content);
    }

    private JComponent createContent(List<PomInfo> pomInfos)
    {
        //todo: use table
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = -1;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        pomInfos.forEach(pomInfo ->
        {
            constraints.gridx = 0;
            constraints.gridy++;
            constraints.weightx = 1;
            constraints.insets = JBUI.insets(2, 2, 0, 0);
            JLabel pomLabel = new JLabel(pomInfo.getArtifactId() + ":", SwingConstants.LEFT);
            panel.add(pomLabel, constraints);

            constraints.gridy++;
            constraints.weightx = 1;
            constraints.insets = JBUI.insets(2, 20, 0, 0);
            String depMgTitle = "Management dependencies:";
            if (pomInfo.getDependenciesManagement().isEmpty())
            {
                depMgTitle += " (EMPTY)";
            }
            JLabel depMgTitleLabel = new JLabel(depMgTitle, SwingConstants.LEFT);
            panel.add(depMgTitleLabel, constraints);

            pomInfo.getDependenciesManagement().forEach(dependencyInfo ->
            {
                constraints.gridy++;
                constraints.weightx = 0.3;
                constraints.insets = JBUI.insets(2, 40, 0, 0);
                JLabel label = new JLabel(dependencyInfo.getGroupId() + ":" + dependencyInfo.getArtifactId(), SwingConstants.LEFT);
                panel.add(label, constraints);
            });

            constraints.gridy++;
            constraints.weightx = 1;
            constraints.insets = JBUI.insets(2, 10, 0, 0);
            String depTitle = "Dependencies:";
            if (pomInfo.getDependencies().isEmpty())
            {
                depTitle += " (EMPTY)";
            }
            JLabel depTitleLabel = new JLabel(depTitle, SwingConstants.LEFT);
            panel.add(depTitleLabel, constraints);

            pomInfo.getDependencies().forEach(dependencyInfo ->
            {
                constraints.gridy++;
                constraints.weightx = 0.3;
                constraints.insets = JBUI.insets(2, 20, 0, 0);
                JLabel label = new JLabel(dependencyInfo.getGroupId() + ":" + dependencyInfo.getArtifactId(), SwingConstants.LEFT);
                panel.add(label, constraints);
            });
        });

        return panel;
    }
}
