package com.qixalite.spongestart.tasks;

import org.gradle.api.GradleException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class SetupForgeServerTask extends SetupServerTask {

    @Override
    public void setupServer() {
        new File(getLocation(), "setup.jar").delete();

        File forge = new File(getLocation(), "forge-" + getExtension().getMinecraft() + '-' + getExtension().getForge() + "-universal.jar");
        File output = new File(getLocation(), "server.jar");

        forge.renameTo(output);

        new File(getLocation(), "libraries" + File.separatorChar + "net" + File.separatorChar + "minecraftforge").delete();
        new File(getLocation(), "mods" + File.separatorChar + "mod_list.json").delete();

    }

    @Override
    public void tweakServer() {
        super.tweakServer();

        File conf = new File(getLocation(), "config" + File.separatorChar + "forge.cfg");
        conf.getParentFile().mkdirs();
        List<String> lines = Arrays.asList("general {",
                "    B:disableVersionCheck=true",
                "}",
                "version_checking {",
                "    B:Global=false",
                "}"
        );

        try {
            Files.write(conf.toPath(), lines, Charset.defaultCharset());
        } catch (IOException e) {
            throw new GradleException("Failed to tweak forge config: " + e.getMessage());
        }
    }

    @Override
    public void refresh() {
        setLocation(new File(getExtension().getForgeServerFolder()));
        setDescription("Setup a SpongeForge server");
    }
}
