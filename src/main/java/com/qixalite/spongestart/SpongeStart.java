package com.qixalite.spongestart;

import com.qixalite.spongestart.tasks.*;
import org.gradle.BuildListener;
import org.gradle.BuildResult;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ResolvedConfiguration;
import org.gradle.api.initialization.Settings;
import org.gradle.api.invocation.Gradle;
import org.gradle.plugins.ide.idea.model.IdeaModel;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class SpongeStart implements Plugin<Project>  {

    public static final String PROVIDED_SCOPE = "spongeStart_Provided";

//    private File cachedDir;
//    private File startDir;

    @Override
    public void apply(Project project) {
//        this.cachedDir = new File(project.getGradle().getGradleUserHomeDir(), "caches/SpongeStart/");
//        this.startDir = new File(this.cachedDir, "start");
//
//        DownloadTaskV2.setCacheDir(new File(this.cachedDir, "downloads"));

        project.getPlugins().apply("java");
        project.getPlugins().apply("idea");

        project.getExtensions().create("spongestart", SpongeStartExtension.class);

        project.afterEvaluate(projectAfter -> setupTasks((SpongeStartExtension) projectAfter.getExtensions().getByName("spongestart"), project));

        project.getGradle().addBuildListener(new BuildListener() {
            @Override
            public void buildStarted(Gradle gradle) {

            }

            @Override
            public void settingsEvaluated(Settings settings) {

            }

            @Override
            public void projectsLoaded(Gradle gradle) {

            }

            @Override
            public void projectsEvaluated(Gradle gradle) {

            }

            @Override
            public void buildFinished(BuildResult result) {
                //setupIntellij();
            }
        });
    }

    private void setupTasks(SpongeStartExtension extension, Project project){

        extension.setCacheFolder(project.getGradle().getGradleUserHomeDir() + "/caches/SpongeStart/");
        File startDir = new File(extension.getCacheFolder(), "start");

        //generate start task
        GenerateStart generateStartTask = project.getTasks().create("generateStart", GenerateStart.class);
        generateStartTask.setOutputDir(startDir);
        generateStartTask.setGroup(null);

        project.getConfigurations().maybeCreate(PROVIDED_SCOPE);
        project.getDependencies().add("runtime", project.files(startDir));

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

        GenerateIntelijTask generateIntelijForge = project.getTasks().create("generateIntellijForgeTask", GenerateIntelijTask.class);
        generateIntelijForge.setModulename(intellijModule);
        generateIntelijForge.setTaskname("StartForgeServer");
        generateIntelijForge.setWorkingdir(extension.getForgeServerFolder());

        GenerateIntelijTask generateIntelijVanilla = project.getTasks().create("generateIntellijVanillaTask", GenerateIntelijTask.class);
        generateIntelijVanilla.setModulename(intellijModule);
        generateIntelijVanilla.setTaskname("StartVanillaServer");
        generateIntelijVanilla.setWorkingdir(extension.getVanillaServerFolder());

        //Setup Forge task
        SetupForgeServerV2 setupForgeServerV2 = project.getTasks().create("setupForgeServer", SetupForgeServerV2.class);
        setupForgeServerV2.dependsOn(downloadForgeV2, generateStartTask, generateIntelijForge);
        setupForgeServerV2.setLocation(new File(extension.getForgeServerFolder()));
        setupForgeServerV2.setExtension(extension);

        //Setup Vanilla task
        SetupVanillaServerV2 setupVanillaServerV2 = project.getTasks().create("setupVanillaServer", SetupVanillaServerV2.class);
        setupVanillaServerV2.dependsOn(downloadSpongeVanillaV2, generateIntelijVanilla);
        setupVanillaServerV2.setLocation(new File(extension.getVanillaServerFolder()));
        setupVanillaServerV2.setExtension(extension);

        //clean tasks
        project.getTasks().create("cleanVanillaServer", CleanFolderTask.class)
                .setFolder(new File(extension.getVanillaServerFolder()));

        project.getTasks().create("cleanForgeServer", CleanFolderTask.class)
                .setFolder(new File(extension.getForgeServerFolder()));

        project.getTasks().create("cleanSpongeStartCache", CleanFolderTask.class)
                .setFolder(new File(extension.getCacheFolder()));


    }

//    private void applyPlugins(){
//        project.getPlugins().apply("java");
//        project.getPlugins().apply("idea");
//    }

//    private void setupIntellij(){
//        Map<String, Map<String, Collection<Configuration>>> scopes = ((IdeaModel) getProject().getExtensions().getByName("idea"))
//                .getModule().getScopes();
//
//        Configuration compileConfiguration = getProject().getConfigurations().getByName("compile");
//        ResolvedConfiguration resolvedconfig = compileConfiguration.getResolvedConfiguration();
//
//        resolvedconfig.getFirstLevelModuleDependencies().stream().
//                filter(resolvedDependency -> resolvedDependency.getName().startsWith("org.spongepowered")).forEach(
//                spongeApi ->
//                        spongeApi.getAllModuleArtifacts()
//                                .forEach(file ->
//                                        getProject().getDependencies().add(SpongeStart.PROVIDED_SCOPE, file.getModuleVersion().getId().toString())
//                                )
//
//        );
////        addExtraConfiguration(getProject().getConfigurations().stream().filter(c -> c.getName().startsWith("forge")).collect(Collectors.toList()));
//        Configuration provided = getProject().getConfigurations().getByName(SpongeStart.PROVIDED_SCOPE);
//
//        scopes.get("COMPILE").get("minus")
//                .add(provided);
//        scopes.get("PROVIDED").get("plus")
//                .add(provided);
//    }
//
//    private void addExtraConfiguration(List<Configuration> configurations){
//        configurations.stream().filter(Objects::nonNull)
//                .forEach(configuration -> configuration.getResolvedConfiguration()
//                        .getResolvedArtifacts().forEach(dep -> this.getProject().getDependencies()
//                                .add(SpongeStart.PROVIDED_SCOPE, dep.getModuleVersion().getId().toString())));
//
//    }

    private String getintellijModuleName(Project project){
        IdeaModel ideaModel =  ((IdeaModel) project.getExtensions().getByName("idea"));
        //todo find a way to read idea's sourcesets
        return ideaModel.getModule().getName() + "_main";
    }

//    public Project getProject() {
//        return project;
//    }

}
