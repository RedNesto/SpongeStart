package com.qixalite.spongestart.tasks;

import org.gradle.api.GradleException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.stream.Stream;

public class SpongeVanillaMavenDownloadTask extends SpongeDownloadTask {

    @Override
    public String getUrl() {
        String mavenmeta = REPO + "spongevanilla/maven-metadata.xml";

        URLConnection connection;
        try {
            URL url = new URL(mavenmeta);
            connection = url.openConnection();
        } catch (MalformedURLException e) {
            throw new GradleException("Malformed URL: " + mavenmeta + " : " + e.getMessage());
        } catch (IOException e) {
            throw new GradleException("I/O exception while opening a connection" + e.getMessage());
        }

        File cached = new File(getExtension().getCacheFolder(), "downloads" + File.separatorChar + "spongevanilla.xml");

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
                e.printStackTrace();
            }
        }

        String latest = null;

        try (Stream<String> stream = Files.lines(cached.toPath())) {
            latest = stream.skip(6L)
                    .filter(s -> s.contains("<version>" + getExtension().getMinecraft()) && s.contains(getExtension().getApi()))
                    .min((o1, o2) -> 1)
                    .orElse(null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (latest != null) {
            latest = latest.substring(15, latest.length() - 10);
        }

        getProject().getLogger().lifecycle("Latest version: " + latest);

        return REPO + "spongevanilla/" + latest + "/spongevanilla-" + latest + ".jar";

    }

    @Override
    public void refresh() {
        super.refresh();
        setDestination(new File(getExtension().getVanillaServerFolder(), "server.jar"));
        setDescription("Download SpongeVanilla jar");
    }

    @Override
    protected String getArtifactVersion() {
        return getExtension().getSpongeVanilla();
    }

}
