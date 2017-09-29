package com.qixalite.spongestart.tasks;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.gradle.api.GradleException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class SpongeDownloadTaskV2 extends DownloadTaskV2 {

    private String artifact;
    private static final String DOWNLOAD_API = "https://dl-api.spongepowered.org/v1/org.spongepowered/";
    private static final String REPO = "https://repo.spongepowered.org/maven/org/spongepowered/";

    public void setArtifact(String artifact) {
        this.artifact = artifact;
    }

    @Override
    public String getDownloadUrl() {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        String artifactVersion = getArtifactVersion();

        if (!artifactVersion.isEmpty()) {
            if (artifact.equalsIgnoreCase("spongeforge")) {
                HttpGet req = new HttpGet(DOWNLOAD_API + artifact + "/downloads/" + artifact);
                try {
                    setForgeDependencies(new JSONObject(EntityUtils.toString(client.execute(req).getEntity())));
                    client.close();
                } catch (IOException e) {
                    throw new GradleException("Failed to obtain specific version: " + artifact + " : " + e.getMessage());
                }

            }
            return REPO + artifact + "/" + artifactVersion + "/" + artifact + "-" + artifactVersion + ".jar";
        }

        HttpGet request = new HttpGet(DOWNLOAD_API + artifact + "/downloads?type=" + getExtension().getType() + "&minecraft=" + getExtension().getMinecraft() + "&limit=1");

        try {
            JSONObject content= (new JSONArray(EntityUtils.toString(client.execute(request).getEntity()))).getJSONObject(0);
            if (artifact.equalsIgnoreCase("spongeforge")) {
                setForgeDependencies(content);
            }
            client.close();
            return content.getJSONObject("artifacts").getJSONObject("").getString("url");
        } catch (IOException e) {
            throw new GradleException("Failed to get info on latest version: " + e.getMessage());
        }

    }

    private String getArtifactVersion() {
        return artifact.equalsIgnoreCase("spongeforge") ? getExtension().getSpongeForgeVersion() : getExtension().getSpongeVanillaVersion();
    }

    private void setForgeDependencies(JSONObject obj) {
        getExtension().setForge(obj.getJSONObject("dependencies").getString("forge"));
        getExtension().setMinecraft(obj.getJSONObject("dependencies").getString("minecraft"));
    }



}
