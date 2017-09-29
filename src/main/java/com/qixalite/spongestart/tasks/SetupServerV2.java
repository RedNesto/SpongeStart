package com.qixalite.spongestart.tasks;

import com.qixalite.spongestart.SpongeStartExtension;
import org.apache.commons.io.FileUtils;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public abstract class SetupServerV2 extends SpongeStartTask{

    private File location;
    private SpongeStartExtension ext;

    public final File getLocation() {
        return location;
    }

    public final void setLocation(File location) {
        this.location = location;
    }


    public final SpongeStartExtension getExtension() {
        return this.ext;
    }

    public final void setExtension(SpongeStartExtension ext) {
        this.ext = ext;
    }

    @TaskAction
    public void doStuff() {
        acceptEula();
        tweakServer();
        setupServer();

    }

    private void acceptEula() {
        try {
            FileUtils.writeStringToFile(new File(location, "eula.txt"), "eula=true", Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tweakServer() {
        File prop = new File(location, "server.properties");
        try {
            FileUtils.writeStringToFile(prop, "max-tick-time=-1", Charset.defaultCharset());
            FileUtils.writeStringToFile(prop, "\nsnooper-enabled=false", Charset.defaultCharset(), true);
            FileUtils.writeStringToFile(prop, "\nallow-flight=true", Charset.defaultCharset(), true);
            FileUtils.writeStringToFile(prop, "\nonline-mode=" + ext.getOnline(), Charset.defaultCharset(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract void setupServer();



}
