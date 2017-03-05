package com.touwolf.plugin.idea.depschecker.helper;

import com.intellij.openapi.vfs.VirtualFile;
import com.touwolf.plugin.idea.depschecker.gradle.GradleBuild;
import com.touwolf.plugin.idea.depschecker.model.DependencyInfo;
import com.touwolf.plugin.idea.depschecker.model.GradleInfo;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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
            GradleBuild build = GradleBuild.read(file.getInputStream());
            return GradleInfo.of(path, build);
        }
        catch (IOException ex)
        {
            LOG.log(Level.SEVERE, ex.getMessage());
            return null;
        }
    }

    public static void upgradeDependency(@NotNull VirtualFile baseDir, @NotNull DependencyInfo dependencyInfo)
    {
        List<VirtualFile> gradleFiles = VirtualFileHelper.findFiles(baseDir, "build.gradle");
        gradleFiles.forEach(file ->
        {
            try
            {
                GradleBuild build = GradleBuild.read(file.getInputStream());
                boolean upgraded = build.upgradeDependency(dependencyInfo.getGroupId(), dependencyInfo.getArtifactId(), dependencyInfo.getLatestVersion());
                if (upgraded)
                {
                    StringBuilder content = new StringBuilder();
                    build.getContent().forEach(line -> content.append(line).append("\n"));
                    VirtualFileHelper.save(file, content.toString());
                }
            }
            catch (IOException ex)
            {
                LOG.log(Level.SEVERE, ex.getMessage());
            }
        });
    }
}
