package com.techyourchance.mockitofundamentals.exercise5;

import static com.techyourchance.mockitofundamentals.exercise5.UpdateUsernameUseCaseSync.*;
import static com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.techyourchance.mockitofundamentals.exercise5.eventbus.EventBusPoster;
import com.techyourchance.mockitofundamentals.exercise5.eventbus.UserDetailsChangedEvent;
import com.techyourchance.mockitofundamentals.exercise5.networking.NetworkErrorException;
import com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync;
import com.techyourchance.mockitofundamentals.exercise5.users.User;
import com.techyourchance.mockitofundamentals.exercise5.users.UsersCache;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

public class UpdateUsernameUseCaseSyncTest {

    private static final String USER_ID = "userId";
    private static final String USERNAME = "username";

    private UpdateUsernameUseCaseSync SUT;

    private UpdateUsernameHttpEndpointSync updateUsernameHttpEndpointSyncMock;
    private UsersCache usersCacheMock;
    private EventBusPoster eventBusPosterMock;

    @Before
    public void setUp() throws Exception {
        updateUsernameHttpEndpointSyncMock = mock(UpdateUsernameHttpEndpointSync.class);
        usersCacheMock = mock(UsersCache.class);
        eventBusPosterMock = mock(EventBusPoster.class);

        SUT = new UpdateUsernameUseCaseSync(
                updateUsernameHttpEndpointSyncMock,
                usersCacheMock,
                eventBusPosterMock
        );
    }

    @Test
    public void updateUsernameSync_success_userIdAndUsernamePassedToEndpoint() throws Exception {
        success();
        SUT.updateUsernameSync(USER_ID, USERNAME);

        final ArgumentCaptor<String> argsCaptor = ArgumentCaptor.forClass(String.class);
        verify(updateUsernameHttpEndpointSyncMock).updateUsername(
                argsCaptor.capture(),
                argsCaptor.capture()
        );

        final List<String> capturedArgs = argsCaptor.getAllValues();
        assertThat(capturedArgs.get(0), is(USER_ID));
        assertThat(capturedArgs.get(1), is(USERNAME));
    }

    @Test
    public void updateUsernameSync_success_userCached() throws Exception {
        success();
        SUT.updateUsernameSync(USER_ID, USERNAME);

        final ArgumentCaptor<User> argsCaptor = ArgumentCaptor.forClass(User.class);
        verify(usersCacheMock).cacheUser(argsCaptor.capture());

        final User capturedArg = argsCaptor.getValue();
        assertThat(capturedArg.getUserId(), is(USER_ID));
        assertThat(capturedArg.getUsername(), is(USERNAME));
    }

    @Test
    public void updateUsernameSync_generalError_userNotCached() throws Exception {
        generalError();
        SUT.updateUsernameSync(USER_ID, USERNAME);

        verifyNoMoreInteractions(usersCacheMock);
    }

    @Test
    public void updateUsernameSync_authError_userNotCached() throws Exception {
        authError();
        SUT.updateUsernameSync(USER_ID, USERNAME);

        verifyNoMoreInteractions(usersCacheMock);
    }

    @Test
    public void updateUsernameSync_serverError_userNotCached() throws Exception {
        serverError();
        SUT.updateUsernameSync(USER_ID, USERNAME);

        verifyNoMoreInteractions(usersCacheMock);
    }

    @Test
    public void updateUsernameSync_success_userDetailsChangedEventPosted() throws Exception {
        success();
        SUT.updateUsernameSync(USER_ID, USERNAME);

        final ArgumentCaptor<Object> argsCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventBusPosterMock).postEvent(argsCaptor.capture());

        final Object capturedArg = argsCaptor.getValue();
        assertThat(capturedArg, is(instanceOf(UserDetailsChangedEvent.class)));
    }

    @Test
    public void updateUsernameSync_generalError_noInteractionsWithEventBusPoster() throws Exception {
        generalError();
        SUT.updateUsernameSync(USER_ID, USERNAME);

        verifyNoMoreInteractions(eventBusPosterMock);
    }

    @Test
    public void updateUsernameSync_authError_noInteractionsWithEventBusPoster() throws Exception {
        authError();
        SUT.updateUsernameSync(USER_ID, USERNAME);

        verifyNoMoreInteractions(eventBusPosterMock);
    }

    @Test
    public void updateUsernameSync_serverError_noInteractionsWithEventBusPoster() throws Exception {
        serverError();
        SUT.updateUsernameSync(USER_ID, USERNAME);

        verifyNoMoreInteractions(eventBusPosterMock);
    }

    @Test
    public void updateUsernameSync_success_successReturned() throws Exception {
        success();
        final UseCaseResult result = SUT.updateUsernameSync(USER_ID, USERNAME);

        assertThat(result, is(UseCaseResult.SUCCESS));
    }

    @Test
    public void updateUsernameSync_generalError_failureReturned() throws Exception {
        generalError();
        final UseCaseResult result = SUT.updateUsernameSync(USER_ID, USERNAME);

        assertThat(result, is(UseCaseResult.FAILURE));
    }

    @Test
    public void updateUsernameSync_authError_failureReturned() throws Exception {
        authError();
        final UseCaseResult result = SUT.updateUsernameSync(USER_ID, USERNAME);

        assertThat(result, is(UseCaseResult.FAILURE));
    }

    @Test
    public void updateUsernameSync_serverError_failureReturned() throws Exception {
        serverError();
        final UseCaseResult result = SUT.updateUsernameSync(USER_ID, USERNAME);

        assertThat(result, is(UseCaseResult.FAILURE));
    }

    @Test
    public void updateUsernameSync_networkError_networkErrorReturned() throws Exception {
        networkError();
        final UseCaseResult result = SUT.updateUsernameSync(USER_ID, USERNAME);

        assertThat(result, is(UseCaseResult.NETWORK_ERROR));
    }

    private void success() throws Exception {
        when(updateUsernameHttpEndpointSyncMock.updateUsername(
                anyString(),
                anyString()
        )).thenReturn(new EndpointResult(
                EndpointResultStatus.SUCCESS,
                USER_ID,
                USERNAME
        ));
    }

    private void generalError() throws Exception {
        when(updateUsernameHttpEndpointSyncMock.updateUsername(
                anyString(),
                anyString()
        )).thenReturn(new EndpointResult(
                EndpointResultStatus.GENERAL_ERROR,
                "",
                ""
        ));
    }

    private void authError() throws Exception {
        when(updateUsernameHttpEndpointSyncMock.updateUsername(
                anyString(),
                anyString()
        )).thenReturn(new EndpointResult(
                EndpointResultStatus.AUTH_ERROR,
                "",
                ""
        ));
    }

    private void serverError() throws Exception {
        when(updateUsernameHttpEndpointSyncMock.updateUsername(
                anyString(),
                anyString()
        )).thenReturn(new EndpointResult(
                EndpointResultStatus.SERVER_ERROR,
                "",
                ""
        ));
    }

    private void networkError() throws NetworkErrorException {
        doThrow(
                new NetworkErrorException()
        ).when(updateUsernameHttpEndpointSyncMock).updateUsername(
                anyString(),
                anyString()
        );
    }
}