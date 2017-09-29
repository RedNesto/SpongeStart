package com.qixalite.spongestart.tasks;

import org.apache.commons.io.FileUtils;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;

public class CleanFolderTask extends SpongeStartTask {

    private File folder;

    public void setFolder(File folder) {
        this.folder = folder;
    }

    @TaskAction
    public void doStuff() {
        try {
            FileUtils.cleanDirectory(folder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
