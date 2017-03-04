package com.touwolf.plugin.idea.depschecker.gradle;

import java.util.LinkedList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class GradleBuild
{
    private final List<String> content;

    private final List<GradleDependency> dependencies;

    public GradleBuild(@NotNull List<String> content)
    {
        this.content = content;
        dependencies = new LinkedList<>();
        initDependencies();
    }

    private void initDependencies()
    {
        int startLine = -1;
        int startBlockLine = -1;
        for (int line = 0; line < content.size(); line++)
        {
            String statement = content.get(line);
            if (startLine < 0 && statement.contains("dependencies"))
            {
                startLine = line;
            }
            if (startLine >= 0 && statement.contains("{"))
            {
                startBlockLine = line;
            }
            if (startBlockLine >= 0 && statement.contains("}"))
            {
                parseDependencies(startBlockLine, line);
                startLine = -1;
                startBlockLine = -1;
            }
        }
    }

    private void parseDependencies(int fromLine, int toLine)
    {
        for (int line = fromLine; line <= toLine; line++)
        {
            GradleDependency dependency = GradleDependency.of(content.get(line));
            if (dependency != null)
            {
                dependencies.add(dependency);
            }
        }
    }

    public List<GradleDependency> getDependencies()
    {
        return dependencies;
    }
}
