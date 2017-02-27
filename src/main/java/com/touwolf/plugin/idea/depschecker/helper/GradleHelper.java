package com.touwolf.plugin.idea.depschecker.helper;

import com.intellij.openapi.vfs.VirtualFile;
import com.touwolf.plugin.idea.depschecker.model.DependencyInfo;
import com.touwolf.plugin.idea.depschecker.model.GradleInfo;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.BlockStatement;
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
        Map<String, VirtualFile> gradleFiles = VirtualFileHelper.findMapFiles(baseDir, "build.gradle");
        gradleFiles.forEach((path, gradleFile) ->
        {
            GradleInfo gradle = parseGradle(path, gradleFile);
            if (gradle != null)
            {
                gradles.add(gradle);
            }
        });
        return gradles;
    }

    @Nullable
    private static GradleInfo parseGradle(@NotNull String path, @NotNull VirtualFile file)
    {
        try
        {
            SourceUnit unit = SourceUnit.create("gradle", VirtualFileHelper.read(file));
            unit.parse();
            unit.completePhase();
            unit.convert();
            return parseGradleModule(path, unit.getAST());
        }
        catch (IOException | CompilationFailedException ex)
        {
            LOG.log(Level.SEVERE, ex.getMessage());
            return null;
        }
    }

    @NotNull
    private static GradleInfo parseGradleModule(@NotNull String path, @NotNull ModuleNode module)
    {
        List<Statement> statements = module.getStatementBlock().getStatements();
        MethodCallExpression depsExpr = GradleHelper.findMethod(statements, "dependencies");
        List<DependencyInfo> dependencies = new LinkedList<>();
        if (depsExpr != null && depsExpr.getArguments() instanceof ArgumentListExpression)
        {
            ArgumentListExpression depsList = (ArgumentListExpression) depsExpr.getArguments();
            depsList.getExpressions().forEach(expression ->
            {
                List<DependencyInfo> depInfos = extractDependencyInfos(expression);
                dependencies.addAll(depInfos);
            });
        }
        GradleInfo info = new GradleInfo(path, "", "");//fixme
        info.getDependencies().addAll(dependencies);
        return info;
    }

    @Nullable
    private static MethodCallExpression findMethod(@NotNull List<Statement> statements, @NotNull String name)
    {
        for (Statement statement : statements)
        {
            MethodCallExpression method = extractMethod(statement, name);
            if (method != null)
            {
                return method;
            }
        }
        return null;
    }

    @Nullable
    private static MethodCallExpression extractMethod(@NotNull Statement statement, @NotNull String name)
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
        return null;
    }

    @NotNull
    private static List<DependencyInfo> extractDependencyInfos(@NotNull Expression expression)
    {
        if (!(expression instanceof ClosureExpression))
        {
            return Collections.emptyList();
        }
        ClosureExpression clojure = (ClosureExpression) expression;
        if (clojure.getCode() instanceof BlockStatement)
        {
            BlockStatement block = (BlockStatement) clojure.getCode();
            List<DependencyInfo> depsInfos = new LinkedList<>();
            block.getStatements().forEach(statement ->
            {
                MethodCallExpression method = extractMethod(statement, "compile");
                if (method != null &&
                    method.getArguments() instanceof TupleExpression)//todo: find out when it is a single string containing group:name:version
                {
                    DependencyInfo depInfo = DependencyInfo.parse((TupleExpression) method.getArguments());
                    if (depInfo != null)
                    {
                        depsInfos.add(depInfo);
                    }
                }
            });
            return depsInfos;
        }
        return Collections.emptyList();
    }
}
