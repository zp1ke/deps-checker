package com.touwolf.plugin.idea.depschecker.model;

import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public class GradleInfo extends BaseInfo
{
    private Set<DependencyInfo> dependencies;

    public GradleInfo(@NotNull String groupId, @NotNull String artifactId, @NotNull String version)
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
}
