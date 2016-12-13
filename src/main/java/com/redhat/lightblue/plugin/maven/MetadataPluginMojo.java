package com.redhat.lightblue.plugin.maven;

import java.io.IOException;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.redhat.lightblue.client.LightblueException;
import com.redhat.lightblue.client.http.LightblueHttpClient;
import com.redhat.lightblue.plugin.MetadataPlugin;

@Mojo(name = "metadata", defaultPhase = LifecyclePhase.GENERATE_TEST_RESOURCES)
public class MetadataPluginMojo extends AbstractMojo {

    @Parameter(required = true)
    private Map<String, String> metadata;

    @Parameter(readonly = true, defaultValue = "${project.build.directory}")
    private String metadataDirectory;

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public String getMetadataDirectory() {
        return metadataDirectory;
    }

    public void setMetadataDirectory(String metadataDirectory) {
        this.metadataDirectory = metadataDirectory;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            new MetadataPlugin(new LightblueHttpClient(), getMetadata(), getMetadataDirectory()).run();
        } catch (LightblueException | IOException e) {
            throw new MojoExecutionException("Unable to download metadata from lightblue", e);
        }
    }

}
