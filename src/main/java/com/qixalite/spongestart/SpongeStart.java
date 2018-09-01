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
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;

import java.io.File;

@NonNullApi
public class SpongeStart implements Plugin<Project>  {

    public static final String NAME = "SpongeStart";

    @Override
    public void apply(Project target) {

        target.getPlugins().apply("java");
        target.getPlugins().apply("idea");

        target.getExtensions().create(NAME.toLowerCase(), SpongeStartExtension.class);

        target.afterEvaluate(projectAfter -> {
            setupTasks(target);
        });
        target.getConfigurations().add(target.getConfigurations().create("dummy"));
        target.getDependencies().add("dummy", target.files("G:\\gradle_cache\\caches\\SpongeStart\\downloads\\spongevanilla-1.12.2-7.1.0-BETA-111.jar"));
    }

    private void setupTasks(Project project) {
        SpongeStartExtension extension = project.getExtensions().getByType(SpongeStartExtension.class);

        setupDirs(project, extension);

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


    private void setupDirs(Project project, SpongeStartExtension extension) {
        String cacheDir = extension.getCacheFolder();
        if (cacheDir == null) {
            cacheDir = project.getGradle().getGradleUserHomeDir().getAbsolutePath() + File.separator + "caches" + File.separatorChar + NAME;
            extension.setCacheFolder(cacheDir);
        }
        new File(cacheDir).mkdirs();

        String buildDir = extension.getBuildClassesFolder();
        if (buildDir == null) {
            buildDir = project.getBuildDir().getAbsolutePath();
            extension.setBuildClassesFolder(buildDir);
        }

        String resDir = extension.getResourcesFolder();
        if (resDir == null) {
            SourceSet set = project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME);
            resDir = set.getOutput().getResourcesDir().getAbsolutePath();
            extension.setResourcesFolder(resDir);
        }

        String forgeDir = extension.getForgeServerFolder();
        if (forgeDir == null) {
            forgeDir = project.getProjectDir().getAbsolutePath() + File.separatorChar + "run" + File.separatorChar + "forge";
            extension.setForgeServerFolder(forgeDir);
        }

        String vanillaDir = extension.getVanillaServerFolder();
        if (vanillaDir == null) {
            vanillaDir = project.getProjectDir().getAbsolutePath() + File.separatorChar + "run" + File.separatorChar + "vanilla";
            extension.setVanillaServerFolder(vanillaDir);
        }
    }

}
