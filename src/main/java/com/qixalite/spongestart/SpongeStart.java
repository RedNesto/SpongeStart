package com.qixalite.spongestart;

import com.qixalite.spongestart.tasks.CleanFolderTask;
import com.qixalite.spongestart.tasks.ForgeDownloadTaskV2;
import com.qixalite.spongestart.tasks.GenerateRunTaskV2;
import com.qixalite.spongestart.tasks.GenerateStart;
import com.qixalite.spongestart.tasks.SetupForgeServerV2;
import com.qixalite.spongestart.tasks.SetupVanillaServerV2;
import com.qixalite.spongestart.tasks.SpongeDownloadTaskV2;
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
        GenerateStart generateStartTask = project.getTasks().create("generateStart", GenerateStart.class);
        generateStartTask.setOutputDir(start);
        generateStartTask.setGroup(null);

        project.getConfigurations().maybeCreate(PROVIDED_SCOPE);
        project.getDependencies().add("runtime", project.files(start));

        //SpongeForge Download Task
        SpongeDownloadTaskV2 downloadSpongeForgeV2 = project.getTasks().create("downloadSpongeForge", SpongeDownloadTaskV2.class);
        downloadSpongeForgeV2.setLocation(new File(extension.getForgeServerFolder(), "mods" + File.separator + "sponge.jar"));
        downloadSpongeForgeV2.setExtension(extension);
        downloadSpongeForgeV2.setArtifact("spongeforge");

        //SpongeVanilla Download Task
        SpongeDownloadTaskV2 downloadSpongeVanillaV2 = project.getTasks().create("downloadSpongeVanilla", SpongeDownloadTaskV2.class);
        downloadSpongeVanillaV2.setLocation(new File(extension.getVanillaServerFolder(), "server.jar"));
        downloadSpongeVanillaV2.setExtension(extension);
        downloadSpongeVanillaV2.setArtifact("spongevanilla");

        //Forge Download Task
        ForgeDownloadTaskV2 downloadForgeV2 = project.getTasks().create("downloadForge", ForgeDownloadTaskV2.class);
        downloadForgeV2.dependsOn(downloadSpongeForgeV2);
        downloadForgeV2.setLocation(new File(extension.getForgeServerFolder(), "setup.jar"));
        downloadForgeV2.setExtension(extension);

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


        GenerateRunTaskV2 generateVanillaRun = project.getTasks().create("GenerateVanillaRun", GenerateRunTaskV2.class);
        generateVanillaRun.setModule(intellijModule);
        generateVanillaRun.setName("StartVanillaServer");
        generateVanillaRun.setDir("file://$PROJECT_DIR$/"+extension.getVanillaServerFolder());
        generateVanillaRun.setMain("org.spongepowered.server.launch.VersionCheckingMain");
        generateVanillaRun.setPargs("--scan-classpath");
        generateVanillaRun.setVargs(s.toString());

        GenerateRunTaskV2 generateForgeRun = project.getTasks().create("GenerateForgeRun", GenerateRunTaskV2.class);
        generateForgeRun.setModule(intellijModule);
        generateForgeRun.setName("StartForgeServer");
        generateForgeRun.setDir("file://$PROJECT_DIR$/"+extension.getForgeServerFolder());
        generateForgeRun.setMain("StartServer");

        //Setup Forge task
        SetupForgeServerV2 setupForgeServerV2 = project.getTasks().create("setupForgeServer", SetupForgeServerV2.class);
        setupForgeServerV2.dependsOn(downloadForgeV2, generateStartTask, generateForgeRun);
        setupForgeServerV2.setLocation(new File(extension.getForgeServerFolder()));
        setupForgeServerV2.setExtension(extension);

        //Setup Vanilla task
        SetupVanillaServerV2 setupVanillaServerV2 = project.getTasks().create("setupVanillaServer", SetupVanillaServerV2.class);
        setupVanillaServerV2.dependsOn(downloadSpongeVanillaV2, generateVanillaRun);
        setupVanillaServerV2.setLocation(new File(extension.getVanillaServerFolder()));
        setupVanillaServerV2.setExtension(extension);

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
