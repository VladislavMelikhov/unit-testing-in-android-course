package com.techyourchance.testdrivendevelopment.exercise8;

import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;

import java.util.List;

public interface FetchContactsUseCase {

    void fetchContacts(String filterTerm);

    void registerListener(Listener listener);

    void unregisterListener(Listener listener);

    enum FailureReason {
        GENERAL_ERROR,
        NETWORK_ERROR
    }

    interface Listener {

        void onFetchContactsSucceeded(List<Contact> contacts);

        void onFetchContactsFailed(FailureReason reason);
    }
}
