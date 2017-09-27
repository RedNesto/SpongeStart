package com.qixalite.spongestart.tasks;

import org.apache.commons.io.FileUtils;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;

public class SetupForgeServer extends SpongeStartTask {

    private File folder = new File("run");

    public void setFolder(File folder) {
        if (folder != null)
            this.folder = folder;
    }

    @TaskAction
    private void setupForge(){
        try {
            this.getLogger().lifecycle("Starting setup");

            Process pr = new ProcessBuilder()
                    .command("java -jar setup.jar --installServer".split(" "))
                    .directory(this.folder)
                    .redirectErrorStream(true)
                    .start();


            try {
                pr.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            FileUtils.deleteQuietly(new File(this.folder, "setup.jar"));

            for (File file : folder.listFiles((dir, name) -> name.endsWith("-universal.jar"))) {
                file.renameTo(new File(this.folder, "server.jar"));
            }

            FileUtils.deleteDirectory(new File(this.folder, "libraries/net/minecraftforge"));
            FileUtils.deleteQuietly(new File(this.folder, "mods/mod_list.json"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
