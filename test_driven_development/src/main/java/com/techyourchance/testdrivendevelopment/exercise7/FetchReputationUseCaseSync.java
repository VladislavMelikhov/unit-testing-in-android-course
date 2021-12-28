package com.techyourchance.testdrivendevelopment.exercise7;

public interface FetchReputationUseCaseSync {

    enum Status {
        SUCCESS,
        FAILURE
    }

    class UseCaseResult {

        private final Status status;
        private final int reputation;

        public UseCaseResult(
                final Status status,
                final int reputation
        ) {
            this.status = status;
            this.reputation = reputation;
        }

        public Status getStatus() {
            return status;
        }

        public int getReputation() {
            return reputation;
        }
    }

    UseCaseResult fetchReputationSync();
}
