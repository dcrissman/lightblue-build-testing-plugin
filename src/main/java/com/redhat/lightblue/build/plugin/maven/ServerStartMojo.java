package com.redhat.lightblue.build.plugin.maven;

import static com.redhat.lightblue.util.JsonUtils.json;
import static com.redhat.lightblue.util.test.AbstractJsonNodeTest.loadJsonNode;

import java.io.FileInputStream;
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
    private String datasourceName;

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
                                    return json(new FileInputStream(path));
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .toArray(JsonNode[]::new);
                }

                @Override
                protected String getDatasource() {
                    return datasourceName;
                }

                @Override
                protected JsonNode getLightblueCrudJson() throws Exception {
                    return json(new FileInputStream(crudJsonPath));
                }

                @Override
                protected JsonNode getLightblueMetadataJson() throws Exception {
                    return json(new FileInputStream(metadataJsonPath));
                }

                @Override
                protected JsonNode getDatasourcesJson() throws Exception {
                    return json(new FileInputStream(datasourcesJsonPath));
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
