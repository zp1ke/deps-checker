package com.touwolf.plugin.idea.depschecker.model;

import com.touwolf.plugin.idea.depschecker.gradle.GradleDependency;
import com.touwolf.plugin.idea.depschecker.helper.MavenHelper;
import java.util.Objects;
import java.util.Properties;
import org.apache.maven.model.Dependency;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.NamedArgumentListExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
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
    public static DependencyInfo parse(@NotNull TupleExpression tupleExpr)
    {
        for (Expression expr : tupleExpr.getExpressions())
        {
            if (expr instanceof NamedArgumentListExpression)
            {
                NamedArgumentListExpression map = (NamedArgumentListExpression) expr;
                String group = null;
                String name = null;
                String version = null;
                for (MapEntryExpression entry : map.getMapEntryExpressions())
                {
                    String key = entry.getKeyExpression().getText();
                    String value = entry.getValueExpression().getText();
                    if ("group".equals(key))
                    {
                        group = value;
                    }
                    else if ("name".equals(key))
                    {
                        name = value;
                    }
                    else if ("version".equals(key))
                    {
                        version = value;
                    }
                }
                if (group != null && name != null && version != null)
                {
                    String lastVersion = MavenHelper.findLatestVersion(group, name);
                    return new DependencyInfo(group, name, version, lastVersion);
                }
            }
        }
        return null;
    }

    @NotNull
    public static DependencyInfo of(@NotNull GradleDependency dependency)
    {
        String group = dependency.getGroup();
        String name = dependency.getName();
        String lastVersion = MavenHelper.findLatestVersion(group, name);
        return new DependencyInfo(group, name, dependency.getVersion(), lastVersion);
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
