package com.rnmaps.maps;


import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.common.ViewUtil;
import com.facebook.react.uimanager.events.Event;

public class MapReadyEvent extends Event {

    public static final String EVENT_NAME = "onMapReady";

    private final String mMessage;

    @Deprecated
    public MapReadyEvent(int viewId, String message) {
      this(-1, viewId, message);
    }
  
    public MapReadyEvent(int surfaceId, int viewId, String message) {
      super(surfaceId, viewId);
      mMessage = message;
    }
  
    public String getMessage() {
        return mMessage;
    }

    @Override
    public String getEventName() {
      return EVENT_NAME;
    }

    @Nullable
    @Override
    protected WritableMap getEventData() {
      WritableMap eventData = Arguments.createMap();
      eventData.putInt("target", getViewTag());
      eventData.putString("message", getMessage());
      return eventData;
    }
}