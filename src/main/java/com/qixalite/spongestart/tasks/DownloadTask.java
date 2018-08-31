package com.qixalite.spongestart.tasks;

import com.qixalite.spongestart.SpongeStartExtension;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public abstract class DownloadTask extends SpongeStartTask implements IRefreshable {

    private File destination;
    private SpongeStartExtension extension;

    @TaskAction
    public void doStuff() {

        String link = getUrl();
        URLConnection connection;
        try {
            URL url = new URL(link);
            connection = url.openConnection();
        } catch (MalformedURLException e) {
            throw new GradleException("Malformed URL: " + link + " : " + e.getMessage());
        } catch (IOException e) {
            throw new GradleException("I/O exception while opening a connection" + e.getMessage());
        }

        long size = connection.getContentLength();


        File cached = new File(extension.getCacheFolder(), "downloads" + File.separatorChar + link.substring(link.lastIndexOf('/') + 1));

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

        destination.getParentFile().mkdirs();
        try {
            Files.copy(cached.toPath().toAbsolutePath(), destination.toPath().toAbsolutePath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    public abstract String getUrl();

    @Override
    public void refresh() {
        new File(extension.getCacheFolder(), "downloads").mkdirs();
    }

    protected final SpongeStartExtension getExtension() {
        return this.extension;
    }

    public final void setExtension(SpongeStartExtension extension) {
        this.extension = extension;
    }

    public File getDestination() {
        return destination;
    }

    public void setDestination(File destination) {
        this.destination = destination;
    }
}
