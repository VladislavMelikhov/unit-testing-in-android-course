package com.techyourchance.unittesting.screens.questiondetails;

import static org.mockito.Mockito.verify;

import com.techyourchance.unittesting.questions.FetchQuestionDetailsUseCase;
import com.techyourchance.unittesting.questions.QuestionDetails;
import com.techyourchance.unittesting.screens.common.screensnavigator.ScreensNavigator;
import com.techyourchance.unittesting.screens.common.toastshelper.ToastsHelper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class QuestionDetailsControllerTest {

    private QuestionDetailsController SUT;

    private FetchQuestionDetailsUseCaseTd fetchQuestionDetailsUseCaseTd;
    private ScreensNavigator screensNavigatorMock;
    private ToastsHelper toastsHelperMock;
    private QuestionDetailsViewMvc questionDetailsViewMvcMock;

    @Before
    public void setUp() throws Exception {
        fetchQuestionDetailsUseCaseTd = new FetchQuestionDetailsUseCaseTd();
        screensNavigatorMock = Mockito.mock(ScreensNavigator.class);
        toastsHelperMock = Mockito.mock(ToastsHelper.class);
        questionDetailsViewMvcMock = Mockito.mock(QuestionDetailsViewMvc.class);

        SUT = new QuestionDetailsController(
                fetchQuestionDetailsUseCaseTd,
                screensNavigatorMock,
                toastsHelperMock
        );
        SUT.bindView(questionDetailsViewMvcMock);
    }

    @Test
    public void onStart_listenersRegistered() throws Exception {

        SUT.onStart();

        verify(questionDetailsViewMvcMock).registerListener(SUT);
        fetchQuestionDetailsUseCaseTd.verifyListenerRegistered(SUT);
    }

    @Test
    public void onStop_listenersUnregistered() throws Exception {

        SUT.onStop();

        verify(questionDetailsViewMvcMock).unregisterListener(SUT);
        fetchQuestionDetailsUseCaseTd.verifyListenerNotRegistered(SUT);
    }

    @Test
    public void onStart_success_questionDetailsBoundToView() throws Exception {
        success();

        SUT.onStart();

        verify(questionDetailsViewMvcMock).bindQuestion(questionDetails());
    }

    @Test
    public void onStart_failure_errorToastShown() throws Exception {
        failure();

        SUT.onStart();

        verify(toastsHelperMock).showUseCaseError();
    }

    @Test
    public void onStart_progressIndicationShown() throws Exception {

        SUT.onStart();

        verify(questionDetailsViewMvcMock).showProgressIndication();
    }

    @Test
    public void onStart_success_progressIndicationHidden() throws Exception {
        success();

        SUT.onStart();

        verify(questionDetailsViewMvcMock).hideProgressIndication();
    }

    @Test
    public void onStart_failure_progressIndicationHidden() throws Exception {
        failure();

        SUT.onStart();

        verify(questionDetailsViewMvcMock).hideProgressIndication();
    }

    @Test
    public void onNavigateUpClicked_navigatedUp() throws Exception {

        SUT.onNavigateUpClicked();

        verify(screensNavigatorMock).navigateUp();
    }

    private void success() {

    }

    private void failure() {
        fetchQuestionDetailsUseCaseTd.mFailure = true;
    }

    private static class FetchQuestionDetailsUseCaseTd extends FetchQuestionDetailsUseCase {

        private boolean mFailure = false;

        public FetchQuestionDetailsUseCaseTd() {
            super(null);
        }

        @Override
        public void fetchQuestionDetailsAndNotify(String questionId) {
            for (FetchQuestionDetailsUseCase.Listener listener : getListeners()) {
                if (mFailure) {
                    listener.onQuestionDetailsFetchFailed();
                } else {
                    listener.onQuestionDetailsFetched(questionDetails());
                }
            }
        }

        public void verifyListenerRegistered(FetchQuestionDetailsUseCase.Listener listener) {
            if (!getListeners().contains(listener)) {
                throw new RuntimeException("listener not registered");
            }
        }

        public void verifyListenerNotRegistered(FetchQuestionDetailsUseCase.Listener listener) {
            if (getListeners().contains(listener)) {
                throw new RuntimeException("listener registered");
            }
        }
    }

    private static QuestionDetails questionDetails() {
        return new QuestionDetails("id", "title", "body");
    }
}