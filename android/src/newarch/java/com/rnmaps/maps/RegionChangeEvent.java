package com.rnmaps.maps;


import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.common.ViewUtil;
import com.facebook.react.uimanager.events.Event;

public class RegionChangeEvent extends Event {

    public static final String EVENT_NAME = "onRegionChange";

    private final WritableMap mData;

    @Deprecated
    public RegionChangeEvent(int viewId, WritableMap data) {
      this(-1, viewId, data);
    }
  
    public RegionChangeEvent(int surfaceId, int viewId, WritableMap data) {
      super(surfaceId, viewId);
      mData = data;
    }

    @Override
    public String getEventName() {
      return EVENT_NAME;
    }

    @Nullable
    @Override
    protected WritableMap getEventData() {
      return mData;
    }
}
