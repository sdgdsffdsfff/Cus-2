package my.android.event;

/**
 * Created by 14110105 on 2015/5/12.
 */
public interface  BaseEvent {


    public static class UiEvent {
        public final int callingId;

        public UiEvent(int callingId) {
            this.callingId = callingId;
        }
    }

    public static class ShowLoadingProgressEvent {
        public final int callingId;
        public final boolean show;
        public final boolean secondary;

        public ShowLoadingProgressEvent(int callingId, boolean show) {
            this(callingId, show, false);
        }

        public ShowLoadingProgressEvent(int callingId, boolean show, boolean secondary) {
            this.callingId = callingId;
            this.show = show;
            this.secondary = secondary;
        }
    }
}
