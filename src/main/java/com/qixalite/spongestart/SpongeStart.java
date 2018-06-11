package com.qixalite.spongestart;

import com.qixalite.spongestart.tasks.ForgeDownloadTask;
import com.qixalite.spongestart.tasks.GenerateForgeRunTask;
import com.qixalite.spongestart.tasks.GenerateVanillaRunTask;
import com.qixalite.spongestart.tasks.SetupForgeServerTask;
import com.qixalite.spongestart.tasks.SetupVanillaServerTask;
import com.qixalite.spongestart.tasks.SpongeForgeMavenDownloadTask;
import com.qixalite.spongestart.tasks.SpongeVanillaMavenDownloadTask;
import org.gradle.api.NonNullApi;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.io.File;
import java.util.Optional;

@NonNullApi
public class SpongeStart implements Plugin<Project>  {

    public static final String NAME = "SpongeStart";

    @Override
    public void apply(Project target) {

        target.getPlugins().apply("java");
        target.getPlugins().apply("idea");

        target.getExtensions().create(NAME.toLowerCase(), SpongeStartExtension.class);

        target.afterEvaluate(projectAfter -> setupTasks(target));
    }

    private void setupTasks(Project project) {
        SpongeStartExtension extension = project.getExtensions().getByType(SpongeStartExtension.class);

        String cacheDir = Optional.ofNullable(extension.getCacheFolder()).orElse(project.getGradle().getGradleUserHomeDir().getPath() + File.separator + "caches" + File.separatorChar + NAME);
        new File(cacheDir).mkdirs();
        extension.setCacheFolder(cacheDir);

        //SpongeForge Download Task
        SpongeForgeMavenDownloadTask downloadSpongeForge = project.getTasks().create("downloadSpongeForge", SpongeForgeMavenDownloadTask.class);
        downloadSpongeForge.setExtension(extension);
        downloadSpongeForge.refresh();

        //SpongeVanilla Download Task
        SpongeVanillaMavenDownloadTask downloadSpongeVanilla = project.getTasks().create("downloadSpongeVanilla", SpongeVanillaMavenDownloadTask.class);
        downloadSpongeVanilla.setExtension(extension);
        downloadSpongeVanilla.refresh();

        //Forge Download Task
        ForgeDownloadTask downloadForge = project.getTasks().create("downloadForge", ForgeDownloadTask.class);
        downloadForge.dependsOn(downloadSpongeForge);
        downloadForge.setExtension(extension);
        downloadForge.refresh();


        //generate intelij tasks
        GenerateVanillaRunTask generateVanillaRun = project.getTasks().create("GenerateVanillaRun", GenerateVanillaRunTask.class);
        generateVanillaRun.setExtension(extension);
        generateVanillaRun.refresh();

        GenerateForgeRunTask generateForgeRun = project.getTasks().create("GenerateForgeRun", GenerateForgeRunTask.class);
        generateForgeRun.setExtension(extension);
        generateForgeRun.refresh();


        //Setup Forge task
        SetupForgeServerTask setupForgeServer = project.getTasks().create("setupForgeServer", SetupForgeServerTask.class);
        setupForgeServer.dependsOn(downloadForge, /*generateStartTask,*/ generateForgeRun);
        setupForgeServer.setExtension(extension);
        setupForgeServer.refresh();

        //Setup Vanilla task
        SetupVanillaServerTask setupVanillaServer = project.getTasks().create("setupVanillaServer", SetupVanillaServerTask.class);
        setupVanillaServer.dependsOn(downloadSpongeVanilla, generateVanillaRun);
        setupVanillaServer.setExtension(extension);
        setupVanillaServer.refresh();



        //TODO
        //clean tasks
//        project.getTasks().create("cleanVanillaServer", CleanFolderTask.class)
//                .setFolder(new File(extension.getVanillaServerFolder()));
//
//        project.getTasks().create("cleanForgeServer", CleanFolderTask.class)
//                .setFolder(new File(extension.getForgeServerFolder()));

        //project.getTasks().create("cleanSpongeStartCache", CleanFolderTask.class)
          //      .setFolder(start);

    }

}
