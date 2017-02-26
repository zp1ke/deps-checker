package com.touwolf.plugin.idea.depschecker.helper;

import com.intellij.openapi.vfs.VirtualFile;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class VirtualFileHelper
{
    @NotNull
    public static List<VirtualFile> findFiles(@NotNull VirtualFile baseDir, @NotNull String filter)
    {
        List<VirtualFile> files = new LinkedList<>();
        if (!baseDir.isDirectory() || baseDir.getName().startsWith("."))
        {
            return files;
        }
        VirtualFile[] children = baseDir.getChildren();
        for (VirtualFile child : children)
        {
            if (child.isDirectory())
            {
                files.addAll(findFiles(child, filter));
            }
            else if (!child.isDirectory() && filter.equals(child.getName()))
            {
                files.add(child);
            }
        }
        return files;
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
}
