package com.techyourchance.testdrivendevelopment.exercise6;

import static com.techyourchance.testdrivendevelopment.exercise6.FetchUserUseCaseSync.*;
import static com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.techyourchance.testdrivendevelopment.exercise6.FetchUserUseCaseSync.UseCaseResult;
import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class FetchUserUseCaseSyncImplTest {

    private static final String USER_ID = "userId";
    private static final String USERNAME = "username";

    private FetchUserUseCaseSync SUT;

    private FetchUserHttpEndpointSync fetchUserHttpEndpointSyncMock;
    private UsersCache usersCacheMock;

    @Before
    public void setUp() throws Exception {
        fetchUserHttpEndpointSyncMock = mock(FetchUserHttpEndpointSync.class);
        usersCacheMock = mock(UsersCache.class);

        SUT = new FetchUserUseCaseSyncImpl(
                fetchUserHttpEndpointSyncMock,
                usersCacheMock
        );
    }

    @Test
    public void fetchUser_anyways_checksUserInCache() throws Exception {
        userInCache();

        SUT.fetchUserSync(USER_ID);

        final ArgumentCaptor<String> argsCaptor = ArgumentCaptor.forClass(String.class);
        verify(usersCacheMock).getUser(argsCaptor.capture());
        final String capturedArg = argsCaptor.getValue();
        assertThat(capturedArg, is(USER_ID));
    }

    @Test
    public void fetchUser_userInCache_noInteractionsWithServer() throws Exception {
        userInCache();

        SUT.fetchUserSync(USER_ID);

        verifyZeroInteractions(fetchUserHttpEndpointSyncMock);
    }

    @Test
    public void fetchUser_userInCache_userFromCacheReturned() throws Exception {
        userInCache();

        final UseCaseResult result = SUT.fetchUserSync(USER_ID);

        assertThat(result.getStatus(), is(Status.SUCCESS));
        assertThat(result.getUser(), is(new User(USER_ID, USERNAME)));
    }

    @Test
    public void fetchUser_userNotInCache_correctParametersPassedToEndpoint() throws Exception {
        userNotInCache();
        requestSuccessful();

        SUT.fetchUserSync(USER_ID);

        final ArgumentCaptor<String> argsCaptor = ArgumentCaptor.forClass(String.class);
        verify(fetchUserHttpEndpointSyncMock).fetchUserSync(argsCaptor.capture());
        final String capturedArg = argsCaptor.getValue();
        assertThat(capturedArg, is(USER_ID));
    }

    @Test
    public void fetchUser_requestSuccessful_userFromNetworkReturned() throws Exception {
        userNotInCache();
        requestSuccessful();

        final UseCaseResult result = SUT.fetchUserSync(USER_ID);

        assertThat(result.getStatus(), is(Status.SUCCESS));
        assertThat(result.getUser(), is(new User(USER_ID, USERNAME)));
    }

    @Test
    public void fetchUser_authError_failureReturned() throws Exception {
        userNotInCache();
        requestFailsWithAuthError();

        final UseCaseResult result = SUT.fetchUserSync(USER_ID);

        assertThat(result.getStatus(), is(Status.FAILURE));
        assertThat(result.getUser(), is(nullValue()));
    }

    @Test
    public void fetchUser_generalError_failureReturned() throws Exception {
        userNotInCache();
        requestFailsWithGeneralError();

        final UseCaseResult result = SUT.fetchUserSync(USER_ID);

        assertThat(result.getStatus(), is(Status.FAILURE));
        assertThat(result.getUser(), is(nullValue()));
    }

    @Test
    public void fetchUser_networkErrorException_networkErrorReturned() throws Exception {
        userNotInCache();
        requestFailsWithNetworkErrorException();

        final UseCaseResult result = SUT.fetchUserSync(USER_ID);

        assertThat(result.getStatus(), is(Status.NETWORK_ERROR));
        assertThat(result.getUser(), is(nullValue()));
    }

    @Test
    public void fetchUser_requestSuccessful_userIsCached() throws Exception {
        userNotInCache();
        requestSuccessful();

        SUT.fetchUserSync(USER_ID);

        final ArgumentCaptor<User> argsCaptor = ArgumentCaptor.forClass(User.class);
        verify(usersCacheMock).cacheUser(argsCaptor.capture());
        final User capturedArg = argsCaptor.getValue();
        assertThat(capturedArg, is(new User(USER_ID, USERNAME)));
    }

    @Test
    public void fetchUser_authError_cacheNotChanged() throws Exception {
        userNotInCache();
        requestFailsWithAuthError();

        SUT.fetchUserSync(USER_ID);

        verify(usersCacheMock, never()).cacheUser(any(User.class));
    }

    @Test
    public void fetchUser_generalError_cacheNotChanged() throws Exception {
        userNotInCache();
        requestFailsWithGeneralError();

        SUT.fetchUserSync(USER_ID);

        verify(usersCacheMock, never()).cacheUser(any(User.class));
    }

    @Test
    public void fetchUser_networkErrorException_cacheNotChanged() throws Exception {
        userNotInCache();
        requestFailsWithNetworkErrorException();

        SUT.fetchUserSync(USER_ID);

        verify(usersCacheMock, never()).cacheUser(any(User.class));
    }

    private void requestSuccessful() throws Exception {
        when(fetchUserHttpEndpointSyncMock.fetchUserSync(
                anyString()
        )).thenReturn(new EndpointResult(
                EndpointStatus.SUCCESS,
                USER_ID,
                USERNAME
        ));
    }

    private void requestFailsWithAuthError() throws Exception {
        when(fetchUserHttpEndpointSyncMock.fetchUserSync(
                anyString()
        )).thenReturn(new EndpointResult(
                EndpointStatus.AUTH_ERROR,
                "",
                ""
        ));
    }

    private void requestFailsWithGeneralError() throws Exception {
        when(fetchUserHttpEndpointSyncMock.fetchUserSync(
                anyString()
        )).thenReturn(new EndpointResult(
                EndpointStatus.GENERAL_ERROR,
                "",
                ""
        ));
    }

    private void requestFailsWithNetworkErrorException() throws Exception {
        when(fetchUserHttpEndpointSyncMock.fetchUserSync(
                anyString()
        )).thenThrow(new NetworkErrorException());
    }

    private void userInCache() throws Exception {
        when(usersCacheMock.getUser(
                anyString()
        )).thenReturn(new User(
                USER_ID,
                USERNAME
        ));
    }

    private void userNotInCache() throws Exception {
        when(usersCacheMock.getUser(
                anyString()
        )).thenReturn(null);
    }
}