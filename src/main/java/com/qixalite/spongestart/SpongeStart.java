package com.qixalite.spongestart;

import com.qixalite.spongestart.tasks.CleanFolderTask;
import com.qixalite.spongestart.tasks.ForgeDownloadTask;
import com.qixalite.spongestart.tasks.GenerateRunTask;
import com.qixalite.spongestart.tasks.GenerateStartTask;
import com.qixalite.spongestart.tasks.SetupForgeServerTask;
import com.qixalite.spongestart.tasks.SetupVanillaServerTask;
import com.qixalite.spongestart.tasks.SpongeDownloadTask;
import org.apache.commons.io.FileUtils;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ResolvedConfiguration;
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
                        throw new GradleException("Not able to retrieve classes dir: " + e.getMessage());
                    }
                }
            }
            throw new GradleException("Classes dir not found");
        });
        String resDir;
        try {
            resDir = Optional.ofNullable(extension.getResourcesFolder()).orElse(set.getOutput().getResourcesDir().getCanonicalPath());
        } catch (IOException e) {
            throw new GradleException("Invalid resource dir: " + e.getMessage());
        }
        String cacheDir = Optional.ofNullable(extension.getCacheFolder()).orElse(project.getGradle().getGradleUserHomeDir() + File.separator + "SpongeStart" + File.separatorChar + "cache");
        String startDir = Optional.ofNullable(extension.getStartFolder()).orElse(project.getGradle().getGradleUserHomeDir() + File.separator + "SpongeStart");

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
        SpongeDownloadTask downloadSpongeForge = project.getTasks().create("downloadSpongeForge", SpongeDownloadTask.class);
        downloadSpongeForge.setLocation(new File(extension.getForgeServerFolder(), "mods" + File.separator + "sponge.jar"));
        downloadSpongeForge.setExtension(extension);
        downloadSpongeForge.setArtifact("spongeforge");
        downloadSpongeForge.setDescription("Download SpongeForge jar");

        //SpongeVanilla Download Task
        SpongeDownloadTask downloadSpongeVanilla = project.getTasks().create("downloadSpongeVanilla", SpongeDownloadTask.class);
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

        StringBuilder s = new StringBuilder("-classpath &quot;$PROJECT_DIR$/run/vanilla/server.jar&quot;:&quot;");

        Configuration compileConfiguration = project.getConfigurations().getByName("compile");
        ResolvedConfiguration resolvedconfig = compileConfiguration.getResolvedConfiguration();

        resolvedconfig.getFirstLevelModuleDependencies().stream().
                filter(resolvedDependency -> !resolvedDependency.getName().startsWith("org.spongepowered:spongeapi")).forEach(
                        dep -> dep.getAllModuleArtifacts().forEach(artifact -> s.append(artifact.getFile().getAbsolutePath()).append("&quot;:&quot;"))
        );
        s.append(resDir).append("&quot;:&quot;").append(classesDir).append("&quot;");


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
