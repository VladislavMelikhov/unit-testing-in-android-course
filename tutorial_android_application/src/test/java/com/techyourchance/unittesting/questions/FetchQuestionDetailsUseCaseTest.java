package com.techyourchance.unittesting.questions;

import com.techyourchance.unittesting.networking.questions.FetchQuestionDetailsEndpoint;
import com.techyourchance.unittesting.networking.questions.QuestionSchema;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class FetchQuestionDetailsUseCaseTest {

    private static final String QUESTION_ID = "questionId";

    private FetchQuestionDetailsUseCase SUT;

    private FetchQuestionDetailsEndpoint fetchQuestionDetailsEndpointMock;

    private FetchQuestionDetailsUseCase.Listener listener1;
    private FetchQuestionDetailsUseCase.Listener listener2;

    @Before
    public void setUp() throws Exception {
        listener1 = mock(FetchQuestionDetailsUseCase.Listener.class);
        listener2 = mock(FetchQuestionDetailsUseCase.Listener.class);
        fetchQuestionDetailsEndpointMock = mock(FetchQuestionDetailsEndpoint.class);
        SUT = new FetchQuestionDetailsUseCase(
                fetchQuestionDetailsEndpointMock
        );
    }

    @Test
    public void fetchQuestionDetailsAndNotify_anyways_correctParametersPassedToEndpoint() {
        //no preconditions

        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID);

        final ArgumentCaptor<String> argsCaptor = ArgumentCaptor.forClass(String.class);
        verify(fetchQuestionDetailsEndpointMock).fetchQuestionDetails(
                argsCaptor.capture(),
                Mockito.<FetchQuestionDetailsEndpoint.Listener>any()
        );
        final String capturedArg = argsCaptor.getValue();
        assertThat(capturedArg, is(QUESTION_ID));
    }

    @Test
    public void fetchQuestionDetailsAndNotify_success_listenersNotifiedWithCorrectData() {
        success();

        SUT.registerListener(listener1);
        SUT.registerListener(listener2);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID);

        final ArgumentCaptor<QuestionDetails> argsCaptor = ArgumentCaptor.forClass(QuestionDetails.class);
        verify(listener1).onQuestionDetailsFetched(argsCaptor.capture());
        verify(listener2).onQuestionDetailsFetched(argsCaptor.capture());
        final List<QuestionDetails> capturedArgs = argsCaptor.getAllValues();
        assertThat(capturedArgs.get(0), is(questionDetails()));
        assertThat(capturedArgs.get(1), is(questionDetails()));
    }

    @Test
    public void fetchQuestionDetailsAndNotify_failure_listenersNotifiedWithFailure() {
        failure();

        SUT.registerListener(listener1);
        SUT.registerListener(listener2);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID);

        verify(listener1).onQuestionDetailsFetchFailed();
        verify(listener2).onQuestionDetailsFetchFailed();
    }

    private void failure() {
        setupEndpoint(new Consumer<FetchQuestionDetailsEndpoint.Listener>() {
            @Override
            public void invoke(FetchQuestionDetailsEndpoint.Listener listener) {
                listener.onQuestionDetailsFetchFailed();
            }
        });
    }

    private void success() {
        setupEndpoint(new Consumer<FetchQuestionDetailsEndpoint.Listener>() {
            @Override
            public void invoke(FetchQuestionDetailsEndpoint.Listener listener) {
                listener.onQuestionDetailsFetched(questionSchema());
            }
        });
    }

    private void setupEndpoint(final Consumer<FetchQuestionDetailsEndpoint.Listener> actionWithListener) {
        Mockito
                .doAnswer(new Answer<Void>() {
                    @Override
                    public Void answer(InvocationOnMock invocation) throws Throwable {
                        final FetchQuestionDetailsEndpoint.Listener listener = invocation.getArgument(1);
                        actionWithListener.invoke(listener);
                        return null;
                    }
                })
                .when(fetchQuestionDetailsEndpointMock).fetchQuestionDetails(
                Mockito.anyString(),
                Mockito.<FetchQuestionDetailsEndpoint.Listener>any()
        );
    }

    private interface Consumer<T> {

        void invoke(T value);
    }

    private QuestionSchema questionSchema() {
        return new QuestionSchema("title1", "id1", "body1");
    }

    private QuestionDetails questionDetails() {
        return new QuestionDetails("id1", "title1", "body1");
    }
}