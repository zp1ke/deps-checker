
package com.touwolf.plugin.idea.depschecker.rest;

import java.util.List;
import java.util.Map;

public class MavenApiHelper
{
    private static final String MAVEN_API_URL = "http://search.maven.org/solrsearch/select?q=g:\"GROUP\"+AND+a:\"ARTIFACT\"&rows=5&wt=json";

    public static String findLatestVersion(String groupId, String artifactId)
    {
        String url = MAVEN_API_URL
            .replace("GROUP", groupId)
            .replace("ARTIFACT", artifactId);
        Map response = RestClient.get(url, Map.class);
        if (response != null)
        {
            List docs = traverseMap(response, List.class, "response", "docs");
            if (docs != null && !docs.isEmpty() &&
                Map.class.isAssignableFrom(docs.get(0).getClass()))
            {
                Map doc = (Map) docs.get(0);
                Object latestVersion = doc.get("latestVersion");
                if (latestVersion != null)
                {
                    return latestVersion.toString();
                }
            }
        }
        return "?";
    }

    @SuppressWarnings("unchecked")
    private static <T> T traverseMap(Map map, Class<T> finalCls, String... keys)
    {
        Map current = map;
        for (int i = 0; i < keys.length - 2; i++)
        {
            Object currentValue = current.get(keys[i]);
            if (currentValue == null || !Map.class.isAssignableFrom(currentValue.getClass()))
            {
                current = null;
                break;
            }
            else
            {
                current = (Map) currentValue;
            }
        }
        if (current != null)
        {
            Object result = current.get(keys[keys.length - 1]);
            if (result != null && finalCls.isAssignableFrom(result.getClass()))
            {
                return (T) result;
            }
        }
        return null;
    }
}
