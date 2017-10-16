package com.qixalite.spongestart;

import com.qixalite.spongestart.tasks.CleanFolderTask;
import com.qixalite.spongestart.tasks.ForgeDownloadTask;
import com.qixalite.spongestart.tasks.GenerateRunTask;
import com.qixalite.spongestart.tasks.GenerateStartTask;
import com.qixalite.spongestart.tasks.SetupForgeServerTask;
import com.qixalite.spongestart.tasks.SetupVanillaServerTask;
import com.qixalite.spongestart.tasks.SpongeDownloadTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ResolvedConfiguration;
import org.gradle.plugins.ide.idea.model.IdeaModel;

import java.io.File;
import java.util.Optional;

public class SpongeStart implements Plugin<Project>  {

    public static final String PROVIDED_SCOPE = "spongeStart_Provided";

    @Override
    public void apply(Project project) {

        project.getPlugins().apply("java");
        project.getPlugins().apply("idea");

        project.getExtensions().create("spongestart", SpongeStartExtension.class);

        project.afterEvaluate(projectAfter -> setupTasks((SpongeStartExtension) projectAfter.getExtensions().getByName("spongestart"), project));
    }

    private void setupTasks(SpongeStartExtension extension, Project project) {

        String buildDir = Optional.ofNullable(extension.getBuildClassesFolder()).orElse(project.getBuildDir().getAbsolutePath() + "/classes/java/main");
        String resDir = Optional.ofNullable(extension.getResourcesFolder()).orElse(project.getBuildDir().getAbsolutePath() + "/resources/main");
        String cacheDir = Optional.ofNullable(extension.getCacheFolder()).orElse(project.getGradle().getGradleUserHomeDir() + "/caches/SpongeStart/");
        String startDir = Optional.ofNullable(extension.getStartFolder()).orElse(project.getGradle().getGradleUserHomeDir() + "/start");

        File start = new File(startDir);

        //generate start task
        GenerateStartTask generateStartTask = project.getTasks().create("generateStart", GenerateStartTask.class);
        generateStartTask.setOutputDir(start);
        generateStartTask.setGroup(null);

        project.getConfigurations().maybeCreate(PROVIDED_SCOPE);
        project.getDependencies().add("runtime", project.files(start));

        //SpongeForge Download Task
        SpongeDownloadTask downloadSpongeForge = project.getTasks().create("downloadSpongeForge", SpongeDownloadTask.class);
        downloadSpongeForge.setLocation(new File(extension.getForgeServerFolder(), "mods" + File.separator + "sponge.jar"));
        downloadSpongeForge.setExtension(extension);
        downloadSpongeForge.setArtifact("spongeforge");

        //SpongeVanilla Download Task
        SpongeDownloadTask downloadSpongeVanilla = project.getTasks().create("downloadSpongeVanilla", SpongeDownloadTask.class);
        downloadSpongeVanilla.setLocation(new File(extension.getVanillaServerFolder(), "server.jar"));
        downloadSpongeVanilla.setExtension(extension);
        downloadSpongeVanilla.setArtifact("spongevanilla");

        //Forge Download Task
        ForgeDownloadTask downloadForge = project.getTasks().create("downloadForge", ForgeDownloadTask.class);
        downloadForge.dependsOn(downloadSpongeForge);
        downloadForge.setLocation(new File(extension.getForgeServerFolder(), "setup.jar"));
        downloadForge.setExtension(extension);

        //generate intelij tasks
        String intellijModule = getintellijModuleName(project);

        StringBuilder s = new StringBuilder("-classpath $PROJECT_DIR$/run/vanilla/server.jar:");

        Configuration compileConfiguration = project.getConfigurations().getByName("compile");
        ResolvedConfiguration resolvedconfig = compileConfiguration.getResolvedConfiguration();

        resolvedconfig.getFirstLevelModuleDependencies().stream().
                filter(resolvedDependency -> !resolvedDependency.getName().startsWith("org.spongepowered:spongeapi")).forEach(
                        dep -> dep.getAllModuleArtifacts().forEach(artifact -> s.append(artifact.getFile().getAbsolutePath()).append(":"))
        );
        s.append(resDir).append(":").append(buildDir);


        GenerateRunTask generateVanillaRun = project.getTasks().create("GenerateVanillaRun", GenerateRunTask.class);
        generateVanillaRun.setModule(intellijModule);
        generateVanillaRun.setName("StartVanillaServer");
        generateVanillaRun.setDir("file://$PROJECT_DIR$/"+extension.getVanillaServerFolder());
        generateVanillaRun.setMain("org.spongepowered.server.launch.VersionCheckingMain");
        generateVanillaRun.setPargs("--scan-classpath");
        generateVanillaRun.setVargs(s.toString());

        GenerateRunTask generateForgeRun = project.getTasks().create("GenerateForgeRun", GenerateRunTask.class);
        generateForgeRun.setModule(intellijModule);
        generateForgeRun.setName("StartForgeServer");
        generateForgeRun.setDir("file://$PROJECT_DIR$/"+extension.getForgeServerFolder());
        generateForgeRun.setMain("StartServer");

        //Setup Forge task
        SetupForgeServerTask setupForgeServer = project.getTasks().create("setupForgeServer", SetupForgeServerTask.class);
        setupForgeServer.dependsOn(downloadForge, generateStartTask, generateForgeRun);
        setupForgeServer.setLocation(new File(extension.getForgeServerFolder()));
        setupForgeServer.setExtension(extension);

        //Setup Vanilla task
        SetupVanillaServerTask setupVanillaServer = project.getTasks().create("setupVanillaServer", SetupVanillaServerTask.class);
        setupVanillaServer.dependsOn(downloadSpongeVanilla, generateVanillaRun);
        setupVanillaServer.setLocation(new File(extension.getVanillaServerFolder()));
        setupVanillaServer.setExtension(extension);

        //clean tasks
        project.getTasks().create("cleanVanillaServer", CleanFolderTask.class)
                .setFolder(new File(extension.getVanillaServerFolder()));

        project.getTasks().create("cleanForgeServer", CleanFolderTask.class)
                .setFolder(new File(extension.getForgeServerFolder()));

        project.getTasks().create("cleanSpongeStartCache", CleanFolderTask.class)
                .setFolder(new File(cacheDir));


    }

    private String getintellijModuleName(Project project){
        IdeaModel ideaModel =  ((IdeaModel) project.getExtensions().getByName("idea"));
        //todo find a way to read idea's sourcesets
        return ideaModel.getModule().getName() + "_main";
    }

}
