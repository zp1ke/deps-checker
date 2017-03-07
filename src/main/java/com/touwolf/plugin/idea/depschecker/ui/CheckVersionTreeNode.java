package com.touwolf.plugin.idea.depschecker.ui;

import com.touwolf.plugin.idea.depschecker.model.BaseInfo;
import com.touwolf.plugin.idea.depschecker.model.DependencyInfo;
import com.touwolf.plugin.idea.depschecker.model.GradleInfo;
import com.touwolf.plugin.idea.depschecker.model.PomInfo;
import javax.swing.tree.DefaultMutableTreeNode;

public class CheckVersionTreeNode extends DefaultMutableTreeNode
{
    private final BaseInfo info;

    public CheckVersionTreeNode(BaseInfo info)
    {
        super(info.getGroupId() + ":" + info.getArtifactId());
        this.info = info;
    }

    public boolean isPom()
    {
        return (info instanceof PomInfo);
    }

    public boolean isGradle()
    {
        return (info instanceof GradleInfo);
    }

    public boolean isDependency()
    {
        return (info instanceof DependencyInfo);
    }

    public String getVersion()
    {
        return info.getVersion();
    }

    public String getToUpgradeVersion()
    {
        if (isDependency())
        {
            DependencyInfo dependency = (DependencyInfo) this.info;
            if (dependency.canUpgrade())
            {
                return dependency.getLatestVersion();
            }
        }
        return null;
    }

    public DependencyInfo getDependency()
    {
        if (isDependency())
        {
            return (DependencyInfo) this.info;
        }
        return null;
    }

    public int upgradableDependencies()
    {
        if (isPom())
        {
            PomInfo pom = (PomInfo) info;
            return pom.getUpgradableDependencies();
        }
        if (isGradle())
        {
            GradleInfo gradle = (GradleInfo) info;
            return gradle.getUpgradableDependencies();
        }
        return 0;
    }
}
