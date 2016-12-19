package com.redhat.lightblue.build.plugin.maven;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.redhat.lightblue.client.LightblueClient;
import com.redhat.lightblue.client.LightblueException;
import com.redhat.lightblue.client.MongoExecution;
import com.redhat.lightblue.client.MongoExecution.ReadPreference;
import com.redhat.lightblue.client.Projection;
import com.redhat.lightblue.client.Query;
import com.redhat.lightblue.client.Update;
import com.redhat.lightblue.client.http.LightblueHttpClient;
import com.redhat.lightblue.client.request.data.DataDeleteRequest;
import com.redhat.lightblue.client.request.data.DataFindRequest;
import com.redhat.lightblue.client.request.data.DataInsertRequest;
import com.redhat.lightblue.client.request.data.DataUpdateRequest;

public class TestServerMojo {

    private LightblueClient client;

    @Before
    public void before() {
        client = new LightblueHttpClient();
    }

    @Test
    public void test() throws LightblueException {
        DataInsertRequest insertRequest = new DataInsertRequest(com.redhat.lightblue.build.plugin.maven.Test.ENTITY_NAME);
        insertRequest.returns(Projection.includeField("_id"));
        insertRequest.create(
                new com.redhat.lightblue.build.plugin.maven.Test("fake", "created"));
        com.redhat.lightblue.build.plugin.maven.Test created = client.data(insertRequest, com.redhat.lightblue.build.plugin.maven.Test.class);

        assertNotNull(created);
        String uuid = created.get_id();
        assertNotNull(uuid);

        com.redhat.lightblue.build.plugin.maven.Test found = find(client, uuid);

        assertNotNull(found);
        assertEquals("fake", found.getHostname());
        assertEquals("created", found.getValue());

        DataUpdateRequest updateRequest = new DataUpdateRequest(com.redhat.lightblue.build.plugin.maven.Test.ENTITY_NAME);
        updateRequest.where(Query.withValue("_id", Query.eq, uuid));
        updateRequest.returns(Projection.excludeFieldRecursively("*"));
        updateRequest.updates(Update.set("value", "updated"));
        client.data(updateRequest);

        found = find(client, uuid);

        assertNotNull(found);
        assertEquals("fake", found.getHostname());
        assertEquals("updated", found.getValue());

        DataDeleteRequest deleteRequest = new DataDeleteRequest(com.redhat.lightblue.build.plugin.maven.Test.ENTITY_NAME);
        deleteRequest.where(
                Query.or(
                        Query.withValue("_id", Query.eq, uuid),
                        Query.withValue("creationDate", Query.lte,
                                Date.from(LocalDateTime
                                        .now()
                                        .minusMinutes(5)
                                        .atZone(ZoneId.systemDefault())
                                        .toInstant()))
                )
        );
        client.data(deleteRequest);

        assertNull(find(client, uuid));
    }

    private com.redhat.lightblue.build.plugin.maven.Test find(LightblueClient client, String uuid) throws LightblueException {
        DataFindRequest findRequest = new DataFindRequest(com.redhat.lightblue.build.plugin.maven.Test.ENTITY_NAME);
        findRequest.select(Projection.includeFieldRecursively("*"));
        findRequest.where(Query.withValue("_id", Query.eq, uuid));
        findRequest.execution(MongoExecution.withReadPreference(ReadPreference.primary));
        return client.data(findRequest, com.redhat.lightblue.build.plugin.maven.Test.class);
    }

}
