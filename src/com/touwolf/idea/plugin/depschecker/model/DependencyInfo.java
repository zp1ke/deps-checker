package com.touwolf.idea.plugin.depschecker.model;

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

    public static DependencyInfo parse(Dependency dependency)
    {
        if (dependency == null)
        {
            return null;
        }
        String version = findVersion(dependency);
        if (version == null)
        {
            return null;
        }
        return new DependencyInfo(dependency.getGroupId(), dependency.getArtifactId(), version);
    }

    private static String findVersion(Dependency dependency)
    {
        if (dependency.getVersion() != null)
        {
            return dependency.getVersion();
        }
        return null;
    }
}
