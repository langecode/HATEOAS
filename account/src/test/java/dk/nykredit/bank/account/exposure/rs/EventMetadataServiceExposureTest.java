package dk.nykredit.bank.account.exposure.rs;

import java.net.URI;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import dk.nykredit.bank.account.exposure.rs.model.EventsMetadataRepresentation;
import dk.nykredit.bank.account.persistence.AccountArchivist;
import dk.nykredit.nic.test.rs.UriBuilderFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class EventMetadataServiceExposureTest {

    @Mock
    AccountArchivist archivist;

    @InjectMocks
    EventFeedMetadataServiceExposure service;

    @Test
    public void testMetadata(){
        UriInfo ui = mock(UriInfo.class);
        when(ui.getBaseUriBuilder()).then(new UriBuilderFactory(URI.create("http://mock")));
        Request request = mock(Request.class);
        Response response = service.getMetadata("application/hal+json", ui, request);
        EventsMetadataRepresentation info = (EventsMetadataRepresentation) response.getEntity();
        assertNotNull(info);
        assertTrue(info.getMetadata().contains("purpose"));
        assertEquals("http://mock/account-events-metadata", info.getSelf().getHref());
    }

    @Test
    public void testVersionedMetadata(){
        UriInfo ui = mock(UriInfo.class);
        when(ui.getBaseUriBuilder()).then(new UriBuilderFactory(URI.create("http://mock")));
        Request request = mock(Request.class);
        Response response = service.getMetadata("application/hal+json;concept=metadata;v=1", ui, request);
        EventsMetadataRepresentation info = (EventsMetadataRepresentation) response.getEntity();
        assertNotNull(info);
        assertTrue(info.getMetadata().contains("purpose"));
        assertEquals("http://mock/account-events-metadata", info.getSelf().getHref());
    }

}
