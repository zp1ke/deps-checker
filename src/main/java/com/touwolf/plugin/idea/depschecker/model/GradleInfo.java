package com.touwolf.plugin.idea.depschecker.model;

import com.touwolf.plugin.idea.depschecker.gradle.GradleBuild;
import org.jetbrains.annotations.NotNull;

public class GradleInfo extends DependenciesHolderInfo
{
    private GradleInfo(@NotNull String groupId, @NotNull String artifactId, @NotNull String version)
    {
        super(groupId, artifactId, version);
    }

    @NotNull
    public static GradleInfo of(@NotNull String path, @NotNull GradleBuild build)
    {
        String name = path.replaceAll("/", ".");
        GradleInfo info = new GradleInfo(build.getGroup(), name, build.getVersion());
        build.getDependencies().forEach(gradleDependency ->
        {
            DependencyInfo dependency = DependencyInfo.of(gradleDependency);
            info.getDependencies().add(dependency);
        });
        return info;
    }
}
