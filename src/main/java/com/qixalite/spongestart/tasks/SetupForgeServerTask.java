package com.qixalite.spongestart.tasks;

import org.apache.commons.io.FileUtils;
import org.gradle.api.GradleException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class SetupForgeServerTask extends SetupServerTask {

    @Override
    public void setupServer() {

        try {
            Process pr = new ProcessBuilder()
                    .command("java -jar setup.jar --installServer".split(" "))
                    .directory(getLocation())
                    .redirectErrorStream(true)
                    .start();

            pr.waitFor();

            FileUtils.deleteQuietly(new File(getLocation(), "setup.jar"));
            FileUtils.moveFile(
                    new File(getLocation(), "forge-" + getExtension().getMinecraft() + "-" + getExtension().getForge() + "-universal.jar"),
                    new File(getLocation(), "server.jar")
            );

            FileUtils.deleteDirectory(new File(getLocation(), "libraries/net/minecraftforge"));
            FileUtils.deleteQuietly(new File(getLocation(), "mods/mod_list.json"));

        } catch (IOException | InterruptedException e) {
            throw new GradleException("Failed setup forge: " + e.getMessage());
        }

    }

    @Override
    public void tweakServer() {
        File prop = new File(getLocation(), "config/forge.cfg");
        try {
            FileUtils.writeStringToFile(prop,
                    "general {\n" +
                    "    B:disableVersionCheck=true\n" +
                    "}\n", Charset.defaultCharset());

            FileUtils.writeStringToFile(prop,
                    "version_checking {\n" +
                    "    B:Global=false\n" +
                    "}\n",
                    Charset.defaultCharset(), true);

        } catch (IOException e) {
            throw new GradleException("Failed to tweak forge config: " + e.getMessage());
        }
    }

}
