package com.touwolf.plugin.idea.depschecker.model;

import com.touwolf.plugin.idea.depschecker.maven.DependencyModel;
import com.touwolf.plugin.idea.depschecker.maven.PomModel;
import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProjectInfo extends DependenciesHolderInfo
{
    private ProjectInfo(@NotNull String groupId, @NotNull String artifactId, @NotNull String version)
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
    public static ProjectInfo parse(@Nullable PomModel model, @Nullable PomModel parent)
    {
        if (model == null || model.getArtifactId() == null)
        {
            return null;
        }
        String groupId = findGroupId(model, parent);
        String version = findVersion(model, parent);
        if (groupId == null || version == null)
        {
            return null;
        }
        ProjectInfo info = new ProjectInfo(groupId, model.getArtifactId(), version);
        Map<String, String> properties = new HashMap<>();
        if (parent != null)
        {
            properties.putAll(parent.getProperties());
        }
        properties.putAll(model.getProperties());
        info.getDependencies()
            .addAll(readDependencies(model.getDependencies(), properties));
        if (model.getDependencyManagement() != null)
        {
            info.getDependenciesManagement()
                .addAll(readDependencies(model.getDependencyManagement().getDependencies(), properties));
        }
        return info;
    }

    private static List<DependencyInfo> readDependencies(List<DependencyModel> dependencies,
                                                         Map<String, String> properties)
    {
        List<DependencyInfo> dependenciesList = new LinkedList<>();
        for (DependencyModel dependency : dependencies)
        {
            DependencyInfo depInfo = DependencyInfo.parse(dependency, properties);
            if (depInfo != null)
            {
                dependenciesList.add(depInfo);
            }
        }
        return dependenciesList;
    }

    @Nullable
    private static String findGroupId(@NotNull PomModel model, @Nullable PomModel parent)
    {
        if (model.getGroupId() != null)
        {
            return model.getGroupId();
        }
        if (parent != null && parent.getGroupId() != null)
        {
            return parent.getGroupId();
        }
        return null;
    }

    @Nullable
    private static String findVersion(@NotNull PomModel model, @Nullable PomModel parent)
    {
        if (model.getVersion() != null)
        {
            return model.getVersion();
        }
        if (parent != null && parent.getVersion() != null)
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
