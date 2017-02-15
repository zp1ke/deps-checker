package com.touwolf.plugin.idea.depschecker;

import com.touwolf.plugin.idea.depschecker.model.DependencyInfo;
import org.jetbrains.annotations.NotNull;

public interface ProjectManager
{
    void upgrade(@NotNull DependencyInfo dependencyInfo);
}
