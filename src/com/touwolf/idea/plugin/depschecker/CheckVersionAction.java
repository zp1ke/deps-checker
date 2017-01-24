package com.touwolf.idea.plugin.depschecker;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.touwolf.idea.plugin.depschecker.model.DependencyInfo;
import com.touwolf.idea.plugin.depschecker.model.PomInfo;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class CheckVersionAction extends AnAction
{
    @Override
    public void actionPerformed(AnActionEvent event)
    {
        Project project = event.getProject();
        if (project == null)
        {
            //todo: empty tool windows
            return;
        }
        List<PomInfo> pomInfos = findPomInfos(project.getBaseDir());
        for (PomInfo pomInfo : pomInfos)
        {
            System.out.println(pomInfo.toString());
            System.out.println("MANAGEMENT DEPENDENCIES:");
            for (DependencyInfo depInfo : pomInfo.getDependenciesManagement())
            {
                System.out.println("    -- " + depInfo.toString());
            }
            System.out.println("DEPENDENCIES:");
            for (DependencyInfo depInfo : pomInfo.getDependencies())
            {
                System.out.println("    -- " + depInfo.toString());
            }
        }
        //ToolWindow toolWindow = event.getData(LangDataKeys.TOOL_WINDOW);
        //Messages.showMessageDialog(project, "Hello, " + project.getName() + " project!\n I am glad to see you.", "Information", Messages.getInformationIcon());
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
}
