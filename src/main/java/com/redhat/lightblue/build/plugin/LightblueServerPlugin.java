package com.redhat.lightblue.build.plugin;

import com.fasterxml.jackson.databind.JsonNode;
import com.redhat.lightblue.client.integration.test.LightblueClientTestHarness;
import com.redhat.lightblue.client.integration.test.LightblueExternalResource.LightblueTestHarnessConfig;

public class LightblueServerPlugin extends LightblueClientTestHarness {

    private final LightblueTestHarnessConfig methods;

    public LightblueServerPlugin(final LightblueTestHarnessConfig methods) throws Exception {
        super();
        this.methods = methods;
    }

    public LightblueServerPlugin(int httpServerPort, final LightblueTestHarnessConfig methods) throws Exception {
        super(httpServerPort);
        this.methods = methods;
    }

    @Override
    protected JsonNode[] getMetadataJsonNodes() throws Exception {
        return methods.getMetadataJsonNodes();
    }

    @Override
    public boolean isGrantAnyoneAccess() {
        return methods.isGrantAnyoneAccess();
    }

    public void run() {

    }

}
