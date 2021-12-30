package com.techyourchance.testdrivendevelopment.exercise8;

import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;

import java.util.ArrayList;
import java.util.List;

public class FetchContactsUseCaseImpl implements FetchContactsUseCase {

    private final GetContactsHttpEndpoint getContactsHttpEndpoint;
    private final List<Listener> listeners = new ArrayList<>();

    public FetchContactsUseCaseImpl(GetContactsHttpEndpoint getContactsHttpEndpoint) {
        this.getContactsHttpEndpoint = getContactsHttpEndpoint;
    }

    @Override
    public void fetchContacts(String filterTerm) {
        getContactsHttpEndpoint.getContacts(filterTerm, new GetContactsHttpEndpoint.Callback() {
            @Override
            public void onGetContactsSucceeded(List<ContactSchema> schemas) {
                final List<Contact> contacts = convertToDomain(schemas);
                for (Listener listener : listeners) {
                    listener.onFetchContactsSucceeded(contacts);
                }
            }

            @Override
            public void onGetContactsFailed(GetContactsHttpEndpoint.FailReason failReason) {
                final FailureReason failureReason = convertToDomain(failReason);
                for (Listener listener : listeners) {
                    listener.onFetchContactsFailed(failureReason);
                }
            }
        });
    }

    @Override
    public void registerListener(Listener listener) {
        listeners.add(listener);
    }

    @Override
    public void unregisterListener(Listener listener) {
        listeners.remove(listener);
    }

    private List<Contact> convertToDomain(List<ContactSchema> schemas) {
        final List<Contact> contacts = new ArrayList<>();
        for (ContactSchema schema : schemas) {
            contacts.add(new Contact(
                    schema.getId(),
                    schema.getFullName(),
                    schema.getImageUrl()
            ));
        }
        return contacts;
    }

    private FailureReason convertToDomain(GetContactsHttpEndpoint.FailReason failReason) {
        switch (failReason) {
            case GENERAL_ERROR:
                return FailureReason.GENERAL_ERROR;
            case NETWORK_ERROR:
                return FailureReason.NETWORK_ERROR;
            default:
                throw new IllegalStateException("Unexpected value: " + failReason);
        }
    }
}
