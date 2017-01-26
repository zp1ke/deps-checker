package com.touwolf.idea.plugin.depschecker.model;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;

public class PomInfo extends BaseInfo
{
    private PomInfo(String groupId, String artifactId, String version)
    {
        super(groupId, artifactId, version);
    }

    private Set<DependencyInfo> dependenciesManagement;

    private Set<DependencyInfo> dependencies;

    public Set<DependencyInfo> getDependenciesManagement()
    {
        if (dependenciesManagement == null)
        {
            dependenciesManagement = new HashSet<>();
        }
        return dependenciesManagement;
    }

    public void setDependenciesManagement(Set<DependencyInfo> dependenciesManagement)
    {
        this.dependenciesManagement = dependenciesManagement;
    }

    public Set<DependencyInfo> getDependencies()
    {
        if (dependencies == null)
        {
            dependencies = new HashSet<>();
        }
        return dependencies;
    }

    public void setDependencies(Set<DependencyInfo> dependencies)
    {
        this.dependencies = dependencies;
    }

    public static PomInfo parse(Model model, Model parent)
    {
        if (model == null)
        {
            return null;
        }
        String groupId = findGroupId(model);
        if (groupId == null)
        {
            return null;
        }
        String version = findVersion(model);
        if (version == null)
        {
            return null;
        }
        PomInfo info = new PomInfo(groupId, model.getArtifactId(), version);
        Properties properties = new Properties();
        if (parent != null)
        {
            properties.putAll(parent.getProperties());
        }
        properties.putAll(model.getProperties());
        for (Dependency dependency : model.getDependencies())
        {
            DependencyInfo depInfo = DependencyInfo.parse(dependency, properties);
            if (depInfo != null)
            {
                info.getDependencies().add(depInfo);
            }
        }
        if (model.getDependencyManagement() != null)
        for (Dependency dependency : model.getDependencyManagement().getDependencies())
        {
            DependencyInfo depInfo = DependencyInfo.parse(dependency, properties);
            if (depInfo != null)
            {
                info.getDependenciesManagement().add(depInfo);
            }
        }
        return info;
    }

    private static String findGroupId(Model model)
    {
        if (model.getGroupId() != null)
        {
            return model.getGroupId();
        }
        Parent parent = model.getParent();
        if (parent.getGroupId() != null)
        {
            return parent.getGroupId();
        }
        return null;
    }

    private static String findVersion(Model model)
    {
        if (model.getVersion() != null)
        {
            return model.getVersion();
        }
        Parent parent = model.getParent();
        if (parent.getVersion() != null)
        {
            return parent.getVersion();
        }
        return null;
    }
}
