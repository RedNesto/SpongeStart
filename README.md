# SpongeStart [![forthebadge](https://forthebadge.com/images/badges/contains-cat-gifs.svg)](https://forthebadge.com) 
[![GitHub 
stars](https://img.shields.io/github/stars/ImMorpheus/SpongeStart.svg)](https://github.com/ImMorpheus/SpongeStart/stargazers) [![GitHub 
issues](https://img.shields.io/github/issues/ImMorpheus/SpongeStart.svg)](https://github.com/ImMorpheusSpongeStart/issues) [![Latest Version](https://img.shields.io/badge/gradleplugin-v2.0.2-green.svg)](https://plugins.gradle.org/plugin/com.qixalite.spongestart2)

Gradle plugin to run sponge inside your workspace. Based on the SpongeStart by thomas15v.
I’ve fixed the SpongeVanilla issue and added a few things.

### Major differences

* SpongeVanilla issues should be solved (thanks to RandomByte).
* Removed acceptEula task (Eula is now accepted by default upon server setup, avoiding the creation of an eula.txt in the forge folder while you’re setting up a vanilla server).
* Mercurius is deleted during forge server setup.
* downloadSpongeVanilla is a separate task now.
* New properties in build.gradle (see below).
* Tweaked configs after server setup (see below).
* Minor fixes

### List of tweaks

server.properties:
```
max-tick-time=-1
snooper-enabled=false
allow-flight=true
```

forge.cfg:
```
general {
    B:disableVersionCheck=true
}
version_checking {
    B:Global=false
}
```


## Example Build.gradle for your project
```groovy
plugins {
  id 'com.qixalite.spongestart2' version '2.0.2'
  id "java"
}

spongestart {
   //optional* settings
   minecraft '1.10.2'
   type 'bleeding' //default to stable
   spongeForgeVersion '1.10.2-2202-5.1.0-BETA-2042'
   spongeVanillaVersion '1.10.2-5.0.0-BETA-89'
   online 'false' //default to true (online-mode in server.properties)
}

```
Note: you have to define either minecraft or the sponge specific version for the platform you plan to setup.

## Commands

>`gradle setupServer`
> Generates a forge and vanilla server + intelij run configurations.

>`gradle setupVanilla`
> Generates a vanilla server + intelij run configurations.

>`gradle setupForge`
> Generates a forge server + intelij run configurations.


## Run Configurations for your IDE (in case they don't automatic generate)

### Start SpongeForge Server
>
- **Mainclass**: `StartServer`
- **Working Directory**: `run/forge`

### Start SpongeVanilla Server
>
- **Mainclass**: `StartServer`
- **Working Directory**: `run/vanilla`
- **arguments**: `-scan-classpath`
