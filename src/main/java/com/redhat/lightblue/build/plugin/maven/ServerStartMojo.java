package com.redhat.lightblue.build.plugin.maven;

import static com.redhat.lightblue.util.test.AbstractJsonNodeTest.loadJsonNode;

import java.io.IOException;
import java.util.Arrays;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.fasterxml.jackson.databind.JsonNode;
import com.redhat.lightblue.rest.integration.LightblueRestTestHarness;

@Mojo(name = "server-start", defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST)
public class ServerStartMojo extends AbstractMojo {

    @Parameter(defaultValue = "8000")
    private Integer httpServerPort;

    @Parameter(required = true)
    private String[] metadataPaths;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Starting lightblue server");
        try {
            new LightblueRestTestHarness() {

                @Override
                protected JsonNode[] getMetadataJsonNodes() throws Exception {
                    return Arrays.stream(metadataPaths)
                            .distinct()
                            .map(path -> {
                                try {
                                    return loadJsonNode(path);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .toArray(JsonNode[]::new);
                }

            };
        } catch (Exception e) {
            throw new MojoExecutionException("Unable to start lightblue", e);
        }
    }

}
