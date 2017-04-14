package com.touwolf.plugin.idea.depschecker.model;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PomInfo extends DependenciesHolderInfo
{
    private PomInfo(@NotNull String groupId, @NotNull String artifactId, @NotNull String version)
    {
        super(groupId, artifactId, version);
    }

    private Set<DependencyInfo> dependenciesManagement;

    @NotNull
    public Set<DependencyInfo> getDependenciesManagement()
    {
        if (dependenciesManagement == null)
        {
            dependenciesManagement = new HashSet<>();
        }
        return dependenciesManagement;
    }

    @Nullable
    public static PomInfo parse(@Nullable Model model, @Nullable Model parent)
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

    @Nullable
    private static String findGroupId(@NotNull Model model)
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

    @Nullable
    private static String findVersion(@NotNull Model model)
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

    private int upgradable = -1;

    public int getUpgradableDependenciesManagement()
    {
        if (upgradable < 0)
        {
            upgradable = 0;
            for (DependencyInfo dependency : getDependenciesManagement())
            {
                if (dependency.canUpgrade())
                {
                    upgradable++;
                }
            }
        }
        return upgradable;
    }
}
