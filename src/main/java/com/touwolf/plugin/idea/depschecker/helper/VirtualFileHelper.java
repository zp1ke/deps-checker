package com.touwolf.plugin.idea.depschecker.helper;

import com.intellij.openapi.vfs.VirtualFile;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class VirtualFileHelper
{
    @NotNull
    public static List<VirtualFile> findFiles(@NotNull VirtualFile baseDir, @NotNull String filter)
    {
        Map<String, VirtualFile> mapFiles = findMapFiles(baseDir, filter);
        return new LinkedList<>(mapFiles.values());
    }

    @NotNull
    public static String read(@NotNull VirtualFile file) throws IOException
    {
        InputStreamReader reader = new InputStreamReader(file.getInputStream());
        BufferedReader bufReader = new BufferedReader(reader);
        StringBuilder builder = new StringBuilder();
        String line = bufReader.readLine();
        while (line != null)
        {
            builder.append(line).append("\n");
            line = bufReader.readLine();
        }
        return builder.toString();
    }

    @NotNull
    public static Map<String, VirtualFile> findMapFiles(@NotNull VirtualFile baseDir, @NotNull String filter)
    {
        return findMapFiles(baseDir, filter, "");
    }

    @NotNull
    private static Map<String, VirtualFile> findMapFiles(@NotNull VirtualFile baseDir,
                                                         @NotNull String filter,
                                                         @NotNull String path)
    {
        Map<String, VirtualFile> files = new HashMap<>();
        if (!baseDir.isDirectory() || baseDir.getName().startsWith("."))
        {
            return files;
        }
        VirtualFile[] children = baseDir.getChildren();
        String currentPath = path.isEmpty() ? baseDir.getName() : path + "/" + baseDir.getName();
        for (VirtualFile child : children)
        {
            if (child.isDirectory())
            {
                files.putAll(findMapFiles(child, filter, currentPath));
            }
            else if (!child.isDirectory() && filter.equals(child.getName()))
            {
                files.put(currentPath, child);
            }
        }
        return files;
    }
}
