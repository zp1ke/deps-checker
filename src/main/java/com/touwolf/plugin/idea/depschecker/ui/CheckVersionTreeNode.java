package com.touwolf.plugin.idea.depschecker.ui;

import com.touwolf.plugin.idea.depschecker.model.BaseInfo;
import com.touwolf.plugin.idea.depschecker.model.DependencyInfo;
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
}
