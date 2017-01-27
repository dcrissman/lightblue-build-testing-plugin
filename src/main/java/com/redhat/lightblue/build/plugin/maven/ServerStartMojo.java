package com.redhat.lightblue.build.plugin.maven;

import static com.redhat.lightblue.util.JsonUtils.json;
import static com.redhat.lightblue.util.test.AbstractJsonNodeTest.loadJsonNode;
import static com.redhat.lightblue.util.test.AbstractJsonNodeTest.loadResource;

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

@Mojo(name = "server-start", defaultPhase = LifecyclePhase.PROCESS_TEST_CLASSES)
public class ServerStartMojo extends AbstractMojo {

    @Parameter(defaultValue = "8000")
    private Integer httpServerPort;

    @Parameter(required = true)
    private String[] metadataPaths;

    @Parameter(required = true)
    private String datasource;

    @Parameter(defaultValue = "datasources.json")
    private String datasourcesJsonPath;

    @Parameter(defaultValue = "lightblue-crud.json")
    private String crudJsonPath;

    @Parameter(defaultValue = "lightblue-metadata.json")
    private String metadataJsonPath;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Starting lightblue server");
        try {
            new LightblueRestTestHarness(httpServerPort) {

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

                @Override
                protected String getDatasource() {
                    return datasource;
                }

                @Override
                protected JsonNode getLightblueCrudJson() throws Exception {
                    return loadJsonNode(crudJsonPath);
                }

                @Override
                protected JsonNode getLightblueMetadataJson() throws Exception {
                    return loadJsonNode(metadataJsonPath);
                }

                @Override
                protected JsonNode getDatasourcesJson() throws Exception {
                    return loadJsonNode(datasourcesJsonPath);
                }

            };
        } catch (Exception e) {
            throw new MojoExecutionException("Unable to start lightblue", e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                getLog().info("Stopping lightblue server");
                LightblueRestTestHarness.stopHttpServer();
            }

        });
    }

}
