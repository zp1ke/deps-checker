package com.touwolf.plugin.idea.depschecker.model;

import com.touwolf.plugin.idea.depschecker.gradle.GradleBuild;
import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public class GradleInfo extends BaseInfo
{
    private Set<DependencyInfo> dependencies;

    private GradleInfo(@NotNull String groupId, @NotNull String artifactId, @NotNull String version)
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

    @NotNull
    public static GradleInfo of(@NotNull String path, @NotNull GradleBuild build)
    {
        GradleInfo info = new GradleInfo(path, "", "");
        build.getDependencies().forEach(gradleDependency ->
        {
            DependencyInfo dependency = DependencyInfo.of(gradleDependency);
            info.getDependencies().add(dependency);
        });
        return info;
    }
}
