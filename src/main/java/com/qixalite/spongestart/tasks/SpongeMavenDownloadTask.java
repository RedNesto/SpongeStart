package com.qixalite.spongestart.tasks;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.gradle.api.GradleException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class SpongeMavenDownloadTask extends DownloadTask {

    private String artifact;
    private static final String DOWNLOAD_API = "https://dl-api.spongepowered.org/v1/org.spongepowered/";
    private static final String REPO = "https://repo.spongepowered.org/maven/org/spongepowered/";

    public void setArtifact(String artifact) {
        this.artifact = artifact;
    }

    @Override
    public String getDownloadUrl() {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        String artifactVersion = getArtifactVersion();

        if (artifactVersion == null) {
            String mavenmeta = REPO + artifact + "/maven-metadata.xml";
            HttpGet req = new HttpGet(mavenmeta);

            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                InputSource is = new InputSource(client.execute(req).getEntity().getContent());
                Document doc = builder.parse(is);
                NodeList nList = doc.getElementsByTagName("version");
                String version = null;
                for (int temp = nList.getLength() - 1; temp > -1; temp--) {
                    version = nList.item(temp).getTextContent();
                    if (version.contains(getExtension().getMinecraft()) && version.contains(getExtension().getApi())) {
                        break;
                    }
                }
                getProject().getLogger().lifecycle("Latest version: " + version);
                return REPO + artifact + "/" + version + "/" + artifact + "-" + version + ".jar";

            } catch (ParserConfigurationException | IOException | SAXException e) {
                throw new GradleException(e.getMessage());
            }
        }


        HttpGet request = new HttpGet(DOWNLOAD_API + artifact + "/downloads?type=" + getExtension().getType() + "&minecraft=" + getExtension().getMinecraft() + "&limit=1");

        try {
            JSONObject content= (new JSONArray(EntityUtils.toString(client.execute(request).getEntity()))).getJSONObject(0);
            setDependencies(content);
            client.close();
            return content.getJSONObject("artifacts").getJSONObject("").getString("url");
        } catch (IOException e) {
            throw new GradleException("Failed to get info on latest version: " + e.getMessage());
        }

    }


    private String getArtifactVersion() {
        return artifact.equalsIgnoreCase("spongeforge") ? getExtension().getSpongeForge() : getExtension().getSpongeVanilla();
    }

    private void setDependencies(JSONObject obj) {
        getExtension().setMinecraft(obj.getJSONObject("dependencies").getString("minecraft"));
        if (artifact.equalsIgnoreCase("spongeforge")) {
            getExtension().setForge(obj.getJSONObject("dependencies").getString("forge"));
        }
    }

}
