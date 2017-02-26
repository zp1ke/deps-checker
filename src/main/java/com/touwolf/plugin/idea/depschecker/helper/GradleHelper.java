package com.touwolf.plugin.idea.depschecker.helper;

import com.intellij.openapi.vfs.VirtualFile;
import com.touwolf.plugin.idea.depschecker.model.GradleInfo;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.SourceUnit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GradleHelper
{
    private static final Logger LOG = Logger.getLogger(GradleHelper.class.getName());

    @NotNull
    public static List<GradleInfo> findGradleInfos(@NotNull VirtualFile baseDir)
    {
        List<GradleInfo> gradles = new LinkedList<>();
        List<VirtualFile> gradleFiles = VirtualFileHelper.findFiles(baseDir, "build.gradle");
        gradleFiles.forEach(gradleFile ->
        {
            GradleInfo gradle = parseGradle(gradleFile);
            if (gradle != null)
            {
                gradles.add(gradle);
            }
        });
        return gradles;
    }

    @Nullable
    private static GradleInfo parseGradle(@NotNull VirtualFile file)
    {
        try
        {
            SourceUnit unit = SourceUnit.create("gradle", VirtualFileHelper.read(file));
            unit.parse();
            unit.completePhase();
            unit.convert();
            return GradleInfo.parse(unit.getAST());
        }
        catch (IOException | CompilationFailedException ex)
        {
            LOG.log(Level.SEVERE, ex.getMessage());
            return null;
        }
    }

    @Nullable
    public static MethodCallExpression findMethod(@NotNull List<Statement> statements, @NotNull String name)
    {
        for (Statement statement : statements)
        {
            if (statement instanceof ExpressionStatement)
            {
                Expression expr = ((ExpressionStatement) statement).getExpression();
                if (expr instanceof MethodCallExpression)
                {
                    MethodCallExpression method = (MethodCallExpression) expr;
                    if (method.getMethod() instanceof ConstantExpression)
                    {
                        ConstantExpression methodExpr = (ConstantExpression) method.getMethod();
                        if (name.equals(methodExpr.getValue()))
                        {
                            return method;
                        }
                    }
                }
            }
        }
        return null;
    }
}
