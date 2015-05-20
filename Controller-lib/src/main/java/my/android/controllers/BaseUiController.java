package my.android.controllers;

import com.google.common.base.Preconditions;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import my.android.Display;
import my.android.state.BaseState;

/**
 * BaseUiController
 * Created by 14110105 on 2015/5/12.
 */
public abstract class BaseUiController<U extends BaseUiController.Ui<UC>,
        UC> extends BaseController {

    public interface Ui<UC> {

        void setCallbacks(UC callbacks);

        boolean isModal();
    }

    public interface SubUi {}

    private final Set<U> mUis;
    private final Set<U> mUnmodifiableUis;

    public BaseUiController() {
        mUis = new CopyOnWriteArraySet<>();
        mUnmodifiableUis = Collections.unmodifiableSet(mUis);
    }

    public synchronized final void attachUi(U ui) {
        Preconditions.checkArgument(ui != null, "ui cannot be null");
        Preconditions.checkState(!mUis.contains(ui), "UI is already attached");

        mUis.add(ui);

        ui.setCallbacks(createUiCallbacks(ui));

        if (isInited()) {
            if (!ui.isModal() && !(ui instanceof SubUi)) {
                updateDisplayTitle(getUiTitle(ui));
            }

            onUiAttached(ui);
            populateUi(ui);
        }
    }


    protected final void updateDisplayTitle(String title) {
        Display display = getDisplay();
        if (display != null) {
            display.setActionBarTitle(title);
        }
    }

    protected final void updateDisplayTitle(U ui) {
        updateDisplayTitle(getUiTitle(ui));
    }

    protected String getUiTitle(U ui) {
        return null;
    }

    public synchronized final void detachUi(U ui) {
        Preconditions.checkArgument(ui != null, "ui cannot be null");
        Preconditions.checkState(mUis.contains(ui), "ui is not attached");
        onUiDetached(ui);
        ui.setCallbacks(null);

        mUis.remove(ui);
    }

    protected final Set<U> getUis() {
        return mUnmodifiableUis;
    }

    protected void onInited() {
        if (!mUis.isEmpty()) {
            for (U ui : mUis) {
                onUiAttached(ui);
                populateUi(ui);
            }
        }
    }

    protected void onUiAttached(U ui) {
    }

    protected void onUiDetached(U ui) {
    }


    protected synchronized final void populateUis() {


        for (U ui : mUis) {
            populateUi(ui);
        }
    }

    protected void populateUi(U ui) {
    }

    protected void populateUi(U ui, BaseState.UiCausedEvent event) {

    }

    protected abstract UC createUiCallbacks(U ui);

    protected int getId(U ui) {
        return ui.hashCode();
    }


    protected synchronized U findUi(final int id) {
        for (U ui : mUis) {
            if (getId(ui) == id) {
                return ui;
            }
        }
        return null;
    }

    protected final void populateUiFromEvent(BaseState.UiCausedEvent event) {
        Preconditions.checkNotNull(event, "event cannot be null");

        final U ui = findUi(event.callingId);
        if (ui != null) {
            populateUi(ui, event);
        }
    }

}
