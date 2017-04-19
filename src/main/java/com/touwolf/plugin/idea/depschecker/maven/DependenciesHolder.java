package com.touwolf.plugin.idea.depschecker.maven;

import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class DependenciesHolder
{
    @XmlElementWrapper(name = "dependencies")
    @XmlElements(@XmlElement(name = "dependency", type = DependencyModel.class))
    private List<DependencyModel> dependencies;

    protected void updateDependenciesLines(List<String> lines)
    {
        getDependencies().forEach(dependency ->
        {
            for (int i = dependency.getStartLine(); i <= lines.size(); i++)
            {
                String line = lines.get(i);
                if (line.contains("</dependency>"))
                {
                    dependency.setEndLine(i);
                    break;
                }
            }
        });
    }

    public List<DependencyModel> getDependencies()
    {
        if (dependencies == null)
        {
            dependencies = new LinkedList<>();
        }
        return dependencies;
    }

    public void setDependencies(List<DependencyModel> dependencies)
    {
        this.dependencies = dependencies;
    }
}
