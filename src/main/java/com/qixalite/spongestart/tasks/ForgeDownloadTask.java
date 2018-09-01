package com.qixalite.spongestart.tasks;

import org.gradle.api.GradleException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.stream.Stream;

public class ForgeDownloadTask extends DownloadTask {

    private static final String FORGE_REPO = "https://files.minecraftforge.net/maven/net/minecraftforge/forge/";

    @Override
    public String getUrl() {
        String artifactVersion = getExtension().getForge();
        if (artifactVersion != null && !artifactVersion.isEmpty()) {
            String fullForgeVersion = getExtension().getMinecraft() + "-" + artifactVersion;
            return "https://files.minecraftforge.net/maven/net/minecraftforge/forge/" + fullForgeVersion + "/forge-" + fullForgeVersion + "-installer.jar";
        }

        URLConnection connection;
        try {
            URL url = new URL("http://files.minecraftforge.net/maven/net/minecraftforge/forge/promotions_slim.json");
            connection = url.openConnection();
        } catch (IOException e) {
            throw new GradleException("I/O exception while opening a connection" + e.getMessage());
        }

        File cached = new File(getExtension().getCacheFolder(), "downloads" + File.separatorChar + "forge.json");

        long size = connection.getContentLength();

        if (!cached.exists() || cached.length() != size) {
            cached.delete();
            try (InputStream in = connection.getInputStream();
                 FileOutputStream out = new FileOutputStream(cached)) {

                byte[] buf = new byte[8192];
                int l;
                while (-1 != (l = in.read(buf))) {
                    out.write(buf, 0, l);
                }
            } catch (IOException e) {
                throw new GradleException("IOException while downloading the forge version db " + e.getMessage());
            }
        }

        String latest = null;

        try (Stream<String> stream = Files.lines(cached.toPath())) {
            latest = stream.skip(3L)
                    .filter(s -> s.contains(getExtension().getMinecraft() + "-latest"))
                    .findFirst()
                    .orElse(null);
        } catch (IOException e) {
            throw new GradleException("IOException while reading the forge version db " + e.getMessage());
        }

        if (latest != null) {
            latest = latest.substring(latest.indexOf(':') + 3, latest.length() - 2);
        }

        getProject().getLogger().lifecycle("Latest version: " + latest);

        String key = getExtension().getMinecraft() + '-' + latest;
        getExtension().setForge(latest);
        return FORGE_REPO + key + "/forge-" + key + "-installer.jar";
    }

    @Override
    public void refresh() {
        super.refresh();
        setDestination(new File(getExtension().getForgeServerFolder(), "setup.jar"));
        setDescription("Download Forge jar");
    }
}
