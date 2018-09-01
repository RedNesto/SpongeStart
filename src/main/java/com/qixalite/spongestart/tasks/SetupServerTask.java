package com.qixalite.spongestart.tasks;

import com.qixalite.spongestart.SpongeStartExtension;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class SetupServerTask extends SpongeStartTask implements IRefreshable {

    private File location;
    private SpongeStartExtension extension;

    @TaskAction
    public void doStuff() {
        acceptEula();
        tweakServer();
        setupServer();
    }

    private void acceptEula() {

        List<String> lines = Collections.singletonList("eula=true");

        try {
            Files.write(new File(location, "eula.txt").toPath(), lines, Charset.defaultCharset());
        } catch (IOException e) {
            throw new GradleException("Failed to accept eula: " + e.getMessage());
        }
    }

    public void tweakServer() {
        File prop = new File(location, "server.properties");
        List<String> lines = Arrays.asList("max-tick-time=-1",
                "snooper-enabled=false",
                "allow-flight=true",
                "online-mode=" + extension.getOnline()
        );

        try {
            Files.write(prop.toPath(), lines, Charset.defaultCharset());
        } catch (IOException e) {
            throw new GradleException("Failed to tweak server.properties: " + e.getMessage());
        }
    }

    public abstract void setupServer();

    @OutputDirectory
    public final File getLocation() {
        return location;
    }

    public final void setLocation(File location) {
        this.location = location;
    }


    protected final SpongeStartExtension getExtension() {
        return this.extension;
    }

    public final void setExtension(SpongeStartExtension extension) {
        this.extension = extension;
    }

}
