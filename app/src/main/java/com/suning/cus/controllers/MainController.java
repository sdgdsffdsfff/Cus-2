package com.suning.cus.controllers;

import com.suning.cus.event.RequestFailEvent;

import my.android.controllers.BaseUiController;
import my.android.state.ApplicationState;

/**
 * Created by 14110105 on 2015/5/20.
 */
public class MainController extends BaseUiController<MainController.MainControllerUi, MainController.MainControllerUiCallbacks>{


    public interface HostCallbacks {
        void finish();
    }

    public interface MainControllerUiCallbacks {

        void addAccountRequested();

    }


    public interface MainControllerUi extends BaseUiController.Ui<MainControllerUiCallbacks> {
    }

    public interface MainUi extends MainControllerUi {}

    private final TaskController mTaskController;
    private final ApplicationState mState;
    private HostCallbacks mHostCallbacks;


    public MainController() {
        mState = new ApplicationState();
        this.mTaskController = new TaskController(mState);
    }

    public MainController(ApplicationState state) {
        mState = state;
        this.mTaskController = new TaskController(mState);
    }

    public MainController(TaskController taskController, ApplicationState state) {
        this.mTaskController = taskController;
        mState = state;
    }

    public void setHostCallbacks(HostCallbacks hostCallbacks) {
        mHostCallbacks = hostCallbacks;
    }


    @Override
    protected void onInited() {
        super.onInited();
        mState.registerForEvents(this);
        mTaskController.init();
    }


    @Override
    protected void onSuspended() {
        mTaskController.suspend();
        mState.unregisterForEvents(this);
        super.onSuspended();
    }

    @Override
    protected void populateUi(MainControllerUi ui) {
        super.populateUi(ui);

        if(ui instanceof MainUi) {

        }

    }


    private void populateUi(MainUi ui) {

    }

    protected MainControllerUiCallbacks createUiCallbacks(final MainControllerUi ui) {

        return new MainControllerUiCallbacks() {
            @Override
            public void addAccountRequested() {

            }
        };

    }

    public TaskController getTaskController() {
        return mTaskController;
    }

    public void onEvent(RequestFailEvent event) {

    }

}
