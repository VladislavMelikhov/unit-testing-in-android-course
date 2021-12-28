package com.techyourchance.testdrivendevelopment.exercise7;

import static com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync.*;

import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;

public class FetchReputationUseCaseSyncImpl implements FetchReputationUseCaseSync {

    private final GetReputationHttpEndpointSync getReputationHttpEndpointSync;

    public FetchReputationUseCaseSyncImpl(
            GetReputationHttpEndpointSync getReputationHttpEndpointSync
    ) {
        this.getReputationHttpEndpointSync = getReputationHttpEndpointSync;
    }

    @Override
    public UseCaseResult fetchReputationSync() {
        final EndpointResult endpointResult = getReputationHttpEndpointSync.getReputationSync();

        final EndpointStatus status = endpointResult.getStatus();
        final int reputation = endpointResult.getReputation();

        switch (status) {
            case SUCCESS:
                return new UseCaseResult(Status.SUCCESS, reputation);
            case GENERAL_ERROR:
            case NETWORK_ERROR:
                return new UseCaseResult(Status.FAILURE, 0);
            default:
                throw new RuntimeException("Unknown endpoint status " + status);
        }
    }
}
