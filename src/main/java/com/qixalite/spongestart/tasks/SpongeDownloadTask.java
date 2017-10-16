package com.qixalite.spongestart.tasks;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.Input;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class SpongeDownloadTask extends DownloadTask {

    private String artifact;
    private static final String DOWNLOAD_API = "https://dl-api.spongepowered.org/v1/org.spongepowered/";
    private static final String REPO = "https://repo.spongepowered.org/maven/org/spongepowered/";

    public void setArtifact(String artifact) {
        this.artifact = artifact;
    }

    @Input
    @Override
    public String getDownloadUrl() {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        String artifactVersion = getArtifactVersion();

        if (artifactVersion != null) {
            HttpGet req = new HttpGet(DOWNLOAD_API + artifact + "/downloads/" + artifactVersion);

            try {
                JSONObject con = new JSONObject(EntityUtils.toString(client.execute(req).getEntity()));
                setDependencies(con);
                client.close();
            } catch (IOException e) {
                throw new GradleException("Failed to obtain specific version: " + artifact + " : " + e.getMessage());
            }

            return REPO + artifact + "/" + artifactVersion + "/" + artifact + "-" + artifactVersion + ".jar";
        }

        HttpGet request = new HttpGet(DOWNLOAD_API + artifact + "/downloads?type=" + getExtension().getType() + "&minecraft=" + getExtension().getMinecraft() + "&limit=1");

        try {
            JSONObject content= (new JSONArray(EntityUtils.toString(client.execute(request).getEntity()))).getJSONObject(0);
            setDependencies(content);
            client.close();
            return content.getJSONObject("artifacts").getJSONObject("").getString("url");
        } catch (IOException e) {
            throw new GradleException("Failed to get info on latest version: " + e.getMessage());
        }

    }

    @Input
    private String getArtifactVersion() {
        return artifact.equalsIgnoreCase("spongeforge") ? getExtension().getSpongeForge() : getExtension().getSpongeVanilla();
    }

    private void setDependencies(JSONObject obj) {
        getExtension().setMinecraft(obj.getJSONObject("dependencies").getString("minecraft"));
        if (artifact.equalsIgnoreCase("spongeforge")) {
            getExtension().setForge(obj.getJSONObject("dependencies").getString("forge"));
        }
    }



}
