package com.touwolf.plugin.idea.depschecker.model;

import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public abstract class DependenciesHolderInfo extends BaseInfo
{
    private Set<DependencyInfo> dependencies;

    private int upgradable = -1;

    public DependenciesHolderInfo(@NotNull String groupId, @NotNull String artifactId, @NotNull String version)
    {
        super(groupId, artifactId, version);
    }

    @NotNull
    public Set<DependencyInfo> getDependencies()
    {
        if (dependencies == null)
        {
            dependencies = new HashSet<>();
        }
        return dependencies;
    }

    public int getUpgradableDependencies()
    {
        if (upgradable < 0)
        {
            upgradable = 0;
            for (DependencyInfo dependency : getDependencies())
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
