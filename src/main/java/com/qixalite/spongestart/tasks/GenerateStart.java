package com.qixalite.spongestart.tasks;

import org.apache.commons.io.IOUtils;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class GenerateStart extends SpongeStartTask {

    private static final List<String> files = Arrays.asList("StartServer.class", "StartServer$SpongeClassLoader.class");

    @OutputDirectory
    private File outputDir;

    @TaskAction
    public void doStuff() {

        if (!outputDir.exists()) outputDir.mkdir();

        for (String s : files) {
            InputStream link = this.getClass().getClassLoader().getResourceAsStream(s);
            File outputFile = new File(outputDir, s);
            try {
                IOUtils.copy(link, new FileOutputStream(outputFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setOutputDir(File outputDir) {
        this.outputDir = outputDir;
    }
}
