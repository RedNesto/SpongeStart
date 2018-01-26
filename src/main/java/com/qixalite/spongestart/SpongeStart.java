package com.qixalite.spongestart;

import com.qixalite.spongestart.tasks.CleanFolderTask;
import com.qixalite.spongestart.tasks.ForgeDownloadTask;
import com.qixalite.spongestart.tasks.GenerateRunTask;
import com.qixalite.spongestart.tasks.GenerateStartTask;
import com.qixalite.spongestart.tasks.SetupForgeServerTask;
import com.qixalite.spongestart.tasks.SetupVanillaServerTask;
import com.qixalite.spongestart.tasks.SpongeMavenDownloadTask;
import org.apache.commons.io.FileUtils;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ResolvedConfiguration;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.plugins.ide.idea.model.IdeaModel;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class SpongeStart implements Plugin<Project>  {

    private static final String PROVIDED_SCOPE = "spongeStart_Provided";

    @Override
    public void apply(Project project) {

        project.getPlugins().apply("java");
        project.getPlugins().apply("idea");

        project.getExtensions().create("spongestart", SpongeStartExtension.class);

        project.afterEvaluate(projectAfter -> setupTasks((SpongeStartExtension) projectAfter.getExtensions().getByName("spongestart"), project));
    }

    private void setupTasks(SpongeStartExtension extension, Project project) {

        SourceSet set = project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME);

        String classesDir = Optional.ofNullable(extension.getBuildClassesFolder()).orElseGet(() -> {
            for (File a : set.getOutput().getClassesDirs()) {
                if (a.exists()) {
                    try {
                        return a.getCanonicalPath();
                    } catch (IOException e) {
                        project.getLogger().log(LogLevel.WARN, "Not able to retrieve the path");
                    }
                }
            }
            project.getLogger().log(LogLevel.WARN, "Not able to retrieve classes dir. Did you build your plugin ?");
            return null;
        });
        String resDir;
        String cacheDir;
        String startDir;
        try {
            resDir = Optional.ofNullable(extension.getResourcesFolder()).orElse(set.getOutput().getResourcesDir().getCanonicalPath());
            cacheDir = Optional.ofNullable(extension.getCacheFolder()).orElse(project.getGradle().getGradleUserHomeDir().getCanonicalPath() + File.separator + "SpongeStart" + File.separatorChar + "cache");
            startDir = Optional.ofNullable(extension.getStartFolder()).orElse(project.getGradle().getGradleUserHomeDir().getCanonicalPath() + File.separator + "SpongeStart");
        } catch (IOException e) {
            throw new GradleException("Invalid dir: " + e.getMessage());
        }

        try {
            FileUtils.forceMkdir(new File(cacheDir));
        } catch (IOException e) {
            throw new GradleException("Cannot create the cache directory: " + e.getMessage());
        }
        extension.setCacheFolder(cacheDir);

        //generate start task
        File start = new File(startDir);
        GenerateStartTask generateStartTask = project.getTasks().create("generateStart", GenerateStartTask.class);
        generateStartTask.setOutputDir(start);
        generateStartTask.setGroup(null);

        project.getConfigurations().maybeCreate(PROVIDED_SCOPE);
        project.getDependencies().add("runtime", project.files(start));

        //SpongeForge Download Task
        SpongeMavenDownloadTask downloadSpongeForge = project.getTasks().create("downloadSpongeForge", SpongeMavenDownloadTask.class);
        downloadSpongeForge.setLocation(new File(extension.getForgeServerFolder(), "mods" + File.separatorChar + "sponge.jar"));
        downloadSpongeForge.setExtension(extension);
        downloadSpongeForge.setArtifact("spongeforge");
        downloadSpongeForge.setDescription("Download SpongeForge jar");

        //SpongeVanilla Download Task
        SpongeMavenDownloadTask downloadSpongeVanilla = project.getTasks().create("downloadSpongeVanilla", SpongeMavenDownloadTask.class);
        downloadSpongeVanilla.setLocation(new File(extension.getVanillaServerFolder(), "server.jar"));
        downloadSpongeVanilla.setExtension(extension);
        downloadSpongeVanilla.setArtifact("spongevanilla");
        downloadSpongeVanilla.setDescription("Download SpongeVanilla jar");

        //Forge Download Task
        ForgeDownloadTask downloadForge = project.getTasks().create("downloadForge", ForgeDownloadTask.class);
        downloadForge.dependsOn(downloadSpongeForge);
        downloadForge.setLocation(new File(extension.getForgeServerFolder(), "setup.jar"));
        downloadForge.setExtension(extension);
        downloadForge.setDescription("Download Forge jar");

        //generate intelij tasks
        String intellijModule = getintellijModuleName(project);

        StringBuilder s = new StringBuilder("-classpath \"$PROJECT_DIR$" + File.separatorChar + "run" + File.separatorChar + "vanilla" + File.separatorChar + "server.jar\"" + File.pathSeparatorChar + "\"");

        Configuration compileConfiguration = project.getConfigurations().getByName("compile");
        ResolvedConfiguration resolvedconfig = compileConfiguration.getResolvedConfiguration();

        StringBuilder api = new StringBuilder();
        resolvedconfig.getFirstLevelModuleDependencies().stream().
                filter(resolvedDependency -> resolvedDependency.getName().startsWith("org.spongepowered:spongeapi")).forEach(
                        dep -> api.replace(0, api.length(), dep.getModuleVersion())//extension.setApi(artifact.getName().replaceAll("org.spongepowered:spongeapi:", "").substring(0, )))a
        );
        extension.setApi(api.substring(0, api.lastIndexOf("-")));

        resolvedconfig.getFirstLevelModuleDependencies().stream().
                filter(resolvedDependency -> !resolvedDependency.getName().startsWith("org.spongepowered:spongeapi")).forEach(
                        dep -> dep.getAllModuleArtifacts().forEach(artifact -> s.append(artifact.getFile().getAbsolutePath()).append("\"").append(File.pathSeparatorChar).append("\""))
        );
        s.append(resDir).append("\"").append(File.pathSeparatorChar).append("\"").append(classesDir).append("\"");


        GenerateRunTask generateVanillaRun = project.getTasks().create("GenerateVanillaRun", GenerateRunTask.class);
        generateVanillaRun.setModule(intellijModule);
        generateVanillaRun.setName("StartVanillaServer");
        generateVanillaRun.setDir("file://$PROJECT_DIR$/"+extension.getVanillaServerFolder());
        generateVanillaRun.setMain("org.spongepowered.server.launch.VersionCheckingMain");
        generateVanillaRun.setPargs("--scan-classpath");
        generateVanillaRun.setVargs(s.toString());
        generateVanillaRun.setDescription("Generate Vanilla run configuration to start a SpongeVanilla server");

        GenerateRunTask generateForgeRun = project.getTasks().create("GenerateForgeRun", GenerateRunTask.class);
        generateForgeRun.setModule(intellijModule);
        generateForgeRun.setName("StartForgeServer");
        generateForgeRun.setDir("file://$PROJECT_DIR$/"+extension.getForgeServerFolder());
        generateForgeRun.setMain("StartServer");
        generateForgeRun.setDescription("Generate Forge run configuration to start a SpongeForge server");


        //Setup Forge task
        SetupForgeServerTask setupForgeServer = project.getTasks().create("setupForgeServer", SetupForgeServerTask.class);
        setupForgeServer.dependsOn(downloadForge, generateStartTask, generateForgeRun);
        setupForgeServer.setLocation(new File(extension.getForgeServerFolder()));
        setupForgeServer.setExtension(extension);
        setupForgeServer.setDescription("Setup a SpongeForge server");

        //Setup Vanilla task
        SetupVanillaServerTask setupVanillaServer = project.getTasks().create("setupVanillaServer", SetupVanillaServerTask.class);
        setupVanillaServer.dependsOn(downloadSpongeVanilla, generateVanillaRun);
        setupVanillaServer.setLocation(new File(extension.getVanillaServerFolder()));
        setupVanillaServer.setExtension(extension);
        setupVanillaServer.setDescription("Setup a SpongeVanilla server");

        //clean tasks
        project.getTasks().create("cleanVanillaServer", CleanFolderTask.class)
                .setFolder(new File(extension.getVanillaServerFolder()));

        project.getTasks().create("cleanForgeServer", CleanFolderTask.class)
                .setFolder(new File(extension.getForgeServerFolder()));

        project.getTasks().create("cleanSpongeStartCache", CleanFolderTask.class)
                .setFolder(start);

    }

    private String getintellijModuleName(Project project){
        IdeaModel ideaModel =  ((IdeaModel) project.getExtensions().getByName("idea"));
        return ideaModel.getModule().getName() + "_main";
    }

}
