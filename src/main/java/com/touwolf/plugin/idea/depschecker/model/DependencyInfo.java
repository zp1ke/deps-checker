package com.touwolf.plugin.idea.depschecker.model;

import com.touwolf.plugin.idea.depschecker.rest.MavenApiHelper;
import java.util.Objects;
import java.util.Properties;
import org.apache.maven.model.Dependency;

public class DependencyInfo extends BaseInfo
{
    private final String latestVersion;

    private DependencyInfo(String groupId, String artifactId, String version, String lastVersion)
    {
        super(groupId, artifactId, version);
        this.latestVersion = lastVersion;
    }

    public String getLatestVersion()
    {
        return latestVersion;
    }

    public boolean canUpgrade()
    {
        return !Objects.equals(latestVersion, getVersion());
    }

    public static DependencyInfo parse(Dependency dependency, Properties properties)
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
        String lastVersion = MavenApiHelper.findLatestVersion(groupId, artifactId);
        return new DependencyInfo(groupId, artifactId, version, lastVersion);
    }

    private static String findVersion(Dependency dependency, Properties properties)
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
