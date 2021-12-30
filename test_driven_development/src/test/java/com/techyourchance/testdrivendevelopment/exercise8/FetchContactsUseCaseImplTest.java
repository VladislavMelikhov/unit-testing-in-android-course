package com.techyourchance.testdrivendevelopment.exercise8;

import static com.techyourchance.testdrivendevelopment.exercise8.FetchContactsUseCase.*;
import static com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

public class FetchContactsUseCaseImplTest {

    private static final String FILTER_TERM = "filterTerm";

    private static final String ID = "id";
    private static final String FULL_NAME = "fullName";
    private static final String FULL_PHONE_NUMBER = "fullPhoneNumber";
    private static final String IMAGE_URL = "imageUrl";
    private static final Double AGE = 42.0;

    private FetchContactsUseCase SUT;

    private GetContactsHttpEndpoint getContactsHttpEndpointMock;

    private Listener listener1;
    private Listener listener2;

    @Before
    public void setUp() throws Exception {
        getContactsHttpEndpointMock = mock(GetContactsHttpEndpoint.class);
        listener1 = mock(Listener.class);
        listener2 = mock(Listener.class);

        SUT = new FetchContactsUseCaseImpl(
                getContactsHttpEndpointMock
        );
    }

    @Test
    public void fetchContacts_anyways_correctParametersPassedToEndpoint() {
        // no preconditions

        SUT.fetchContacts(FILTER_TERM);

        final ArgumentCaptor<String> argsCaptor = ArgumentCaptor.forClass(String.class);
        verify(getContactsHttpEndpointMock).getContacts(argsCaptor.capture(), any(Callback.class));
        final String capturedArg = argsCaptor.getValue();
        assertThat(capturedArg, is(FILTER_TERM));
    }

    @Test
    public void fetchContacts_requestSuccessful_registeredListenersNotifiedWithData() {
        requestSuccessful();

        SUT.registerListener(listener1);
        SUT.registerListener(listener2);
        SUT.fetchContacts(FILTER_TERM);

        @SuppressWarnings("unchecked")
        final ArgumentCaptor<List<Contact>> argsCaptor = ArgumentCaptor.forClass(List.class);
        verify(listener1).onFetchContactsSucceeded(argsCaptor.capture());
        verify(listener2).onFetchContactsSucceeded(argsCaptor.capture());
        final List<List<Contact>> capturedArgs = argsCaptor.getAllValues();
        final List<Contact> arg1 = capturedArgs.get(0);
        final List<Contact> arg2 = capturedArgs.get(1);
        assertThat(arg1, is(domainContacts()));
        assertThat(arg2, is(domainContacts()));
    }

    @Test
    public void fetchContacts_requestSuccessful_unregisteredListenersNotNotified() {
        requestSuccessful();

        SUT.registerListener(listener1);
        SUT.registerListener(listener2);
        SUT.unregisterListener(listener2);
        SUT.fetchContacts(FILTER_TERM);

        verify(listener1).onFetchContactsSucceeded(ArgumentMatchers.<Contact>anyList());
        verifyZeroInteractions(listener2);
    }

    @Test
    public void fetchContacts_requestFailsWithGeneralError_registeredListenersNotifiedWithFailure() {
        requestFailsWithGeneralError();

        SUT.registerListener(listener1);
        SUT.registerListener(listener2);
        SUT.fetchContacts(FILTER_TERM);

        final ArgumentCaptor<FailureReason> argsCaptor = ArgumentCaptor.forClass(FailureReason.class);
        verify(listener1).onFetchContactsFailed(argsCaptor.capture());
        verify(listener2).onFetchContactsFailed(argsCaptor.capture());
        final List<FailureReason> capturedArgs = argsCaptor.getAllValues();
        final FailureReason arg1 = capturedArgs.get(0);
        final FailureReason arg2 = capturedArgs.get(1);
        assertThat(arg1, is(FailureReason.GENERAL_ERROR));
        assertThat(arg2, is(FailureReason.GENERAL_ERROR));
    }

    @Test
    public void fetchContacts_requestFailsWithNetworkError_registeredListenersNotifiedWithNetworkError() {
        requestFailsWithNetworkError();

        SUT.registerListener(listener1);
        SUT.registerListener(listener2);
        SUT.fetchContacts(FILTER_TERM);

        final ArgumentCaptor<FailureReason> argsCaptor = ArgumentCaptor.forClass(FailureReason.class);
        verify(listener1).onFetchContactsFailed(argsCaptor.capture());
        verify(listener2).onFetchContactsFailed(argsCaptor.capture());
        final List<FailureReason> capturedArgs = argsCaptor.getAllValues();
        final FailureReason arg1 = capturedArgs.get(0);
        final FailureReason arg2 = capturedArgs.get(1);
        assertThat(arg1, is(FailureReason.NETWORK_ERROR));
        assertThat(arg2, is(FailureReason.NETWORK_ERROR));
    }

    private void requestSuccessful() {
        request(new CallbackConsumer() {
            @Override
            public void invoke(Callback callback) {
                callback.onGetContactsSucceeded(networkContacts());
            }
        });
    }

    private void requestFailsWithGeneralError() {
        request(new CallbackConsumer() {
            @Override
            public void invoke(Callback callback) {
                callback.onGetContactsFailed(FailReason.GENERAL_ERROR);
            }
        });
    }

    private void requestFailsWithNetworkError() {
        request(new CallbackConsumer() {
            @Override
            public void invoke(Callback callback) {
                callback.onGetContactsFailed(FailReason.NETWORK_ERROR);
            }
        });
    }

    private void request(final CallbackConsumer consumer) {
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                final Object[] arguments = invocation.getArguments();
                final Callback callback = (Callback) arguments[1];
                consumer.invoke(callback);
                return null;
            }
        }).when(getContactsHttpEndpointMock).getContacts(anyString(), any(Callback.class));
    }

    private interface CallbackConsumer {

        void invoke(Callback callback);
    }

    private List<ContactSchema> networkContacts() {
        final List<ContactSchema> result = new ArrayList<>();
        result.add(new ContactSchema(ID, FULL_NAME, FULL_PHONE_NUMBER, IMAGE_URL, AGE));
        return result;
    }

    private List<Contact> domainContacts() {
        final List<Contact> result = new ArrayList<>();
        result.add(new Contact(ID, FULL_NAME, IMAGE_URL));
        return result;
    }
}