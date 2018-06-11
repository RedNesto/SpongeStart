//package com.qixalite.spongestart.tasks;
//
//import org.apache.commons.io.FileUtils;
//import org.gradle.api.GradleException;
//import org.gradle.api.tasks.OutputDirectory;
//import org.gradle.api.tasks.SkipWhenEmpty;
//import org.gradle.api.tasks.TaskAction;
//
//import java.io.File;
//import java.io.IOException;
//
//public class CleanFolderTask extends SpongeStartTask {
//
//    private File folder;
//
//    @SkipWhenEmpty
//    @OutputDirectory
//    public File getFolder() {
//        return folder;
//    }
//
//    public void setFolder(File folder) {
//        this.folder = folder;
//    }
//
//    @TaskAction
//    public void doStuff() {
//        try {
//            for (File file : folder.listFiles()) {
//                file.delete();
//            }
//            //FileUtils.cleanDirectory(getFolder());
//        } catch (Exception e) {
//            throw new GradleException("Failed to clean folder: " + e.getMessage());
//        }
//    }
//}
