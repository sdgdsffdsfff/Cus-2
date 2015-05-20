package my.android.state;

import de.greenrobot.event.EventBus;

/**
 * Created by 14110105 on 2015/5/20.
 */
public class ApplicationState implements BaseState {

    private final EventBus mEventBus;


    public ApplicationState() {
        this.mEventBus = EventBus.getDefault();
    }

    public ApplicationState(EventBus eventBus) {
        this.mEventBus = eventBus;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public void registerForEvents(Object receiver) {
        mEventBus.register(receiver);
    }

    @Override
    public void unregisterForEvents(Object receiver) {
        mEventBus.unregister(receiver);
    }
}
