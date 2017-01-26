package com.touwolf.idea.plugin.depschecker.model;

import java.util.Properties;
import org.apache.maven.model.Dependency;

public class DependencyInfo extends BaseInfo
{
    private String lastVersion;

    private Boolean canUpgrade;

    private DependencyInfo(String groupId, String artifactId, String version)
    {
        super(groupId, artifactId, version);
    }

    public String getLastVersion()
    {
        return lastVersion;
    }

    public void setLastVersion(String lastVersion)
    {
        this.lastVersion = lastVersion;
    }

    public Boolean getCanUpgrade()
    {
        return canUpgrade;
    }

    public void setCanUpgrade(Boolean canUpgrade)
    {
        this.canUpgrade = canUpgrade;
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
        return new DependencyInfo(dependency.getGroupId(), dependency.getArtifactId(), version);
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
