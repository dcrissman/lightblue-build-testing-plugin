package com.redhat.lightblue.build.plugin.maven;

import static com.redhat.lightblue.client.integration.test.LightblueTestHarnessConfigFactory.forClasspathJson;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.redhat.lightblue.build.plugin.LightblueServerPlugin;

@Mojo(name = "server", defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST)
public class LightblueServerPluginMojo extends AbstractMojo {

    @Parameter(defaultValue = "8000")
    private Integer httpServerPort;

    @Parameter(required = true)
    private String[] metadataPaths;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            new LightblueServerPlugin(
                    httpServerPort,
                    forClasspathJson(metadataPaths)
            ).run();
        } catch (Exception e) {
            throw new MojoExecutionException("Unable to start lightblue", e);
        }
    }

}
