package com.touwolf.plugin.idea.depschecker.model;

import com.touwolf.plugin.idea.depschecker.helper.GradleHelper;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.stmt.Statement;
import org.jetbrains.annotations.NotNull;

public class GradleInfo extends BaseInfo
{
    private Set<DependencyInfo> dependencies;

    public GradleInfo(@NotNull String groupId, @NotNull String artifactId, @NotNull String version)
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

    public static GradleInfo parse(ModuleNode module)
    {
        //todo: parse file module coordinates
        List<Statement> statements = module.getStatementBlock().getStatements();
        MethodCallExpression dependencies = GradleHelper.findMethod(statements, "dependencies");
        //todo: parse dependencies
        return null;
    }
}
