package com.techyourchance.testdrivendevelopment.exercise7;

import static com.techyourchance.testdrivendevelopment.exercise7.FetchReputationUseCaseSync.*;
import static com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;

import org.junit.Before;
import org.junit.Test;

public class FetchReputationUseCaseSyncImplTest {

    private static final int REPUTATION = 42;

    private FetchReputationUseCaseSync SUT;

    private GetReputationHttpEndpointSync getReputationHttpEndpointSyncMock;

    @Before
    public void setUp() throws Exception {
        getReputationHttpEndpointSyncMock = mock(GetReputationHttpEndpointSync.class);

        SUT = new FetchReputationUseCaseSyncImpl(
                getReputationHttpEndpointSyncMock
        );
    }

    @Test
    public void fetchReputationSync_requestSuccessful_successStatusReturned() {
        requestSuccessful();

        final UseCaseResult result = SUT.fetchReputationSync();

        assertThat(result.getStatus(), is(Status.SUCCESS));
    }

    @Test
    public void fetchReputationSync_requestSuccessful_fetchedReputationReturned() {
        requestSuccessful();

        final UseCaseResult result = SUT.fetchReputationSync();

        assertThat(result.getReputation(), is(REPUTATION));
    }

    @Test
    public void fetchReputationSync_requestFailsWithGeneralError_failureStatusReturned() {
        requestFailsWithGeneralError();

        final UseCaseResult result = SUT.fetchReputationSync();

        assertThat(result.getStatus(), is(Status.FAILURE));
    }

    @Test
    public void fetchReputationSync_requestFailsWithNetworkError_failureStatusReturned() {
        requestFailsWithNetworkError();

        final UseCaseResult result = SUT.fetchReputationSync();

        assertThat(result.getStatus(), is(Status.FAILURE));
    }

    @Test
    public void fetchReputationSync_requestFailsWithGeneralError_zeroReputationReturned() {
        requestFailsWithGeneralError();

        final UseCaseResult result = SUT.fetchReputationSync();

        assertThat(result.getReputation(), is(0));
    }

    @Test
    public void fetchReputationSync_requestFailsWithNetworkError_zeroReputationReturned() {
        requestFailsWithNetworkError();

        final UseCaseResult result = SUT.fetchReputationSync();

        assertThat(result.getReputation(), is(0));
    }

    private void requestSuccessful() {
        when(getReputationHttpEndpointSyncMock.getReputationSync())
                .thenReturn(new EndpointResult(EndpointStatus.SUCCESS, REPUTATION));
    }

    private void requestFailsWithGeneralError() {
        when(getReputationHttpEndpointSyncMock.getReputationSync())
                .thenReturn(new EndpointResult(EndpointStatus.GENERAL_ERROR, 0));
    }

    private void requestFailsWithNetworkError() {
        when(getReputationHttpEndpointSyncMock.getReputationSync())
                .thenReturn(new EndpointResult(EndpointStatus.NETWORK_ERROR, 0));
    }
}