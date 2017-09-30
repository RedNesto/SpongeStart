package com.qixalite.spongestart.tasks;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.gradle.api.GradleException;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class SetupVanillaServerV2 extends SetupServerV2 {

    private static final String MOJANG_SERVER = "https://s3.amazonaws.com/Minecraft.Download/versions/";

    //Need a better solution for when the launchwrapper is updated
    private static final String LAUNCHWRAPPER = "https://libraries.minecraft.net/net/minecraft/launchwrapper/1.12/launchwrapper-1.12.jar";

    @Override
    public void setupServer() {
        String mc = getExtension().getMinecraft();
        String url = MOJANG_SERVER + mc + "/minecraft_server." + mc + ".jar";

        CloseableHttpClient client = HttpClientBuilder.create().build();

        downloadLibrary(client, url, getLocation());
        downloadLibrary(client, LAUNCHWRAPPER, new File(getLocation(), "/libraries/net/minecraft/launchwrapper/1.12"));

        try {
            client.close();
        } catch (IOException ignored) {

        }

    }

    private void downloadLibrary(CloseableHttpClient client, String url, File location) {
        try {
            File cached = new File(getExtension().getCacheFolder(), "downloads/" + url.substring(url.lastIndexOf("/") + 1));
            int size = Integer.valueOf(client.execute(new HttpGet(url)).getLastHeader("Content-Length").getValue());
            if (!(cached.exists() && cached.length() == size)) {
                FileUtils.copyURLToFile(new URL(url), cached);
            }

            FileUtils.copyFile(cached, new File(location + "/" + url.substring(url.lastIndexOf("/") + 1)));
        } catch (IOException e) {
            throw new GradleException("Failed to download: " + url + " : " + e.getMessage());
        }
    }



}
