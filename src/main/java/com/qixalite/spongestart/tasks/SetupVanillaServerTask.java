package com.qixalite.spongestart.tasks;

import org.gradle.api.GradleException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class SetupVanillaServerTask extends SetupServerTask {

    private static final String MOJANG_SERVER = "https://s3.amazonaws.com/Minecraft.Download/versions/";

    //Need a better solution for when the launchwrapper is updated
    private static final String LAUNCHWRAPPER = "https://libraries.minecraft.net/net/minecraft/launchwrapper/1.12/launchwrapper-1.12.jar";

    @Override
    public void setupServer() {
        String mc = getExtension().getMinecraft();
        String url = MOJANG_SERVER + mc + "/minecraft_server." + mc + ".jar";

        downloadLibrary(url, getLocation());
        downloadLibrary(LAUNCHWRAPPER, new File(getLocation(),
                File.separatorChar + "libraries" + File.separatorChar + "net" + File.separatorChar + "minecraft" + File.separatorChar + "launchwrapper" + File.separatorChar + "1.12"));


    }

    private void downloadLibrary(String link, File destination) {

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

        File cached = new File(getExtension().getCacheFolder(), "downloads" + File.separatorChar + link.substring(link.lastIndexOf('/') + 1));

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

        destination.mkdirs();
        try {
            Files.copy(cached.toPath().toAbsolutePath(), Paths.get(destination.getAbsolutePath() + File.separatorChar + cached.getName()), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void refresh() {
        setLocation(new File(getExtension().getVanillaServerFolder()));
        setDescription("Setup a SpongeVanilla server");
    }
}
