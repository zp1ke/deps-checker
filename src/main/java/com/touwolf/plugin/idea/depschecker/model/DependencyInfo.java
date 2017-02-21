package com.touwolf.plugin.idea.depschecker.model;

import com.touwolf.plugin.idea.depschecker.helper.MavenHelper;
import java.util.Objects;
import java.util.Properties;
import org.apache.maven.model.Dependency;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DependencyInfo extends BaseInfo
{
    private final String latestVersion;

    private DependencyInfo(@NotNull String groupId,@NotNull String artifactId, @NotNull String version, @NotNull String lastVersion)
    {
        super(groupId, artifactId, version);
        this.latestVersion = lastVersion;
    }

    @NotNull
    public String getLatestVersion()
    {
        return latestVersion;
    }

    public boolean canUpgrade()
    {
        return !MavenHelper.UNKNOWN_VERSION.equals(latestVersion)
            && !Objects.equals(latestVersion, getVersion());
    }

    @Nullable
    public static DependencyInfo parse(@Nullable Dependency dependency, @NotNull Properties properties)
    {
        if (dependency == null)
        {
            return null;
        }
        String version = findVersion(dependency, properties);
        if (version == null)
        {
            return null;
        }
        String groupId = dependency.getGroupId();
        String artifactId = dependency.getArtifactId();
        String lastVersion = MavenHelper.findLatestVersion(groupId, artifactId);
        return new DependencyInfo(groupId, artifactId, version, lastVersion);
    }

    @Nullable
    private static String findVersion(@NotNull Dependency dependency, @NotNull Properties properties)
    {
        if (dependency.getVersion() != null)
        {
            String version = dependency.getVersion();
            int propStartTag = version.indexOf("${");
            if (propStartTag >= 0)
            {
                int propEndTag = version.indexOf("}", propStartTag);
                if (propEndTag > propStartTag)
                {
                    String propertyName = version.substring(propStartTag + 2, propEndTag);
                    if (properties.containsKey(propertyName))
                    {
                        version = version.substring(0, propStartTag) +
                                properties.getProperty(propertyName) +
                                version.substring(propEndTag + 1);
                        return version;
                    }
                }
            }
            else
            {
                return version;
            }
        }
        return null;
    }
}
