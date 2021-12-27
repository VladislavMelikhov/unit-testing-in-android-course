package com.techyourchance.testdrivendevelopment.exercise6;

import static com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync.*;

import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache;

public class FetchUserUseCaseSyncImpl implements FetchUserUseCaseSync {

    private final FetchUserHttpEndpointSync fetchUserHttpEndpointSync;
    private final UsersCache usersCache;

    public FetchUserUseCaseSyncImpl(
            FetchUserHttpEndpointSync fetchUserHttpEndpointSync,
            UsersCache usersCache
    ) {
        this.fetchUserHttpEndpointSync = fetchUserHttpEndpointSync;
        this.usersCache = usersCache;
    }

    @Override
    public UseCaseResult fetchUserSync(String userId) {

        final EndpointResult endpointResult;

        final User cachedUser = usersCache.getUser(userId);

        if (cachedUser != null) {
            return new UseCaseResult(Status.SUCCESS, cachedUser);
        }

        try {
             endpointResult = fetchUserHttpEndpointSync.fetchUserSync(userId);
        } catch (NetworkErrorException e) {
            return new UseCaseResult(Status.NETWORK_ERROR, null);
        }

        final EndpointStatus resultStatus = endpointResult.getStatus();
        switch (resultStatus) {
            case SUCCESS:
                final User user = new User(
                        endpointResult.getUserId(),
                        endpointResult.getUsername()
                );
                usersCache.cacheUser(user);
                return new UseCaseResult(Status.SUCCESS, user);
            case AUTH_ERROR:
            case GENERAL_ERROR:
                return new UseCaseResult(Status.FAILURE, null);
            default:
                throw new IllegalStateException("Unexpected value: " + resultStatus);
        }
    }
}
