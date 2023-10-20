package com.rnmaps.maps;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.uimanager.LayoutShadowNode;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.events.EventDispatcher;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

@ReactModule(name = MapManager.NAME)
public class MapManager extends ViewGroupManager<MapView> {

  private static final String REACT_CLASS = "AIRMap";
  static final String NAME = "AIRMap";


  private final Map<String, Integer> MAP_TYPES = MapBuilder.of(
      "standard", GoogleMap.MAP_TYPE_NORMAL,
      "satellite", GoogleMap.MAP_TYPE_SATELLITE,
      "hybrid", GoogleMap.MAP_TYPE_HYBRID,
      "terrain", GoogleMap.MAP_TYPE_TERRAIN,
      "none", GoogleMap.MAP_TYPE_NONE
  );

  private final Map<String, Integer> MY_LOCATION_PRIORITY = MapBuilder.of(
          "balanced", Priority.PRIORITY_BALANCED_POWER_ACCURACY,
          "high", Priority.PRIORITY_HIGH_ACCURACY,
          "low", Priority.PRIORITY_LOW_POWER,
          "passive", Priority.PRIORITY_PASSIVE
  );

  private final ReactApplicationContext appContext;
  private MapMarkerManager markerManager;

  protected GoogleMapOptions googleMapOptions;

  public MapManager(ReactApplicationContext context) {
    this.appContext = context;
    this.googleMapOptions = new GoogleMapOptions();
  }

  public MapMarkerManager getMarkerManager() {
    return this.markerManager;
  }
  public void setMarkerManager(MapMarkerManager markerManager) {
    this.markerManager = markerManager;
  }

  @NonNull
  @Override
  public String getName() {
    return NAME;
  }

  @NonNull
  @Override
  protected MapView createViewInstance(ThemedReactContext context) {
    return new MapView(context, this.appContext, this, googleMapOptions);
  }

  private void emitMapError(ThemedReactContext context, String message, String type) {
    WritableMap error = Arguments.createMap();
    error.putString("message", message);
    error.putString("type", type);

    context
        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
        .emit("onError", error);
  }

  @ReactProp(name = "region")
  public void setRegion(MapView view, ReadableMap region) {
    view.setRegion(region);
  }

  @ReactProp(name = "initialRegion")
  public void setInitialRegion(MapView view, ReadableMap initialRegion) {
    view.setInitialRegion(initialRegion);
  }

  @ReactProp(name = "camera")
  public void setCamera(MapView view, ReadableMap camera) {
    view.setCamera(camera);
  }

  @ReactProp(name = "initialCamera")
  public void setInitialCamera(MapView view, ReadableMap initialCamera) {
    view.setInitialCamera(initialCamera);
  }

  @ReactProp(name = "mapType")
  public void setMapType(MapView view, @Nullable String mapType) {
    int typeId = MAP_TYPES.get(mapType);
    view.map.setMapType(typeId);
  }

  @ReactProp(name = "customMapStyleString")
  public void setMapStyle(MapView view, @Nullable String customMapStyleString) {
    view.setMapStyle(customMapStyleString);
  }

  @ReactProp(name = "mapPadding")
  public void setMapPadding(MapView view, @Nullable ReadableMap padding) {
    int left = 0;
    int topOn = 0;
    int right = 0;
    int bottom = 0;
    double density = (double) view.getResources().getDisplayMetrics().density;

    if (padding != null) {
      if (padding.hasKey("left")) {
        left = (int) (padding.getDouble("left") * density);
      }

      if (padding.hasKey("topOn")) {
        topOn = (int) (padding.getDouble("topOn") * density);
      }

      if (padding.hasKey("right")) {
        right = (int) (padding.getDouble("right") * density);
      }

      if (padding.hasKey("bottom")) {
        bottom = (int) (padding.getDouble("bottom") * density);
      }
    }

    view.applyBaseMapPadding(left, topOn, right, bottom);
    view.map.setPadding(left, topOn, right, bottom);
  }

  @ReactProp(name = "showsUserLocation", defaultBoolean = false)
  public void setShowsUserLocation(MapView view, boolean showUserLocation) {
    view.setShowsUserLocation(showUserLocation);
  }

  @ReactProp(name = "userLocationPriority")
  public void setUserLocationPriority(MapView view, @Nullable String accuracy) {
    view.setUserLocationPriority(MY_LOCATION_PRIORITY.get(accuracy));
  }

  @ReactProp(name = "userLocationUpdateInterval", defaultInt = 5000)
  public void setUserLocationUpdateInterval(MapView view, int updateInterval) {
    view.setUserLocationUpdateInterval(updateInterval);
  }

  @ReactProp(name = "userLocationFastestInterval", defaultInt = 5000)
  public void setUserLocationFastestInterval(MapView view, int fastestInterval) {
    view.setUserLocationFastestInterval(fastestInterval);
  }

  @ReactProp(name = "showsMyLocationButton", defaultBoolean = true)
  public void setShowsMyLocationButton(MapView view, boolean showMyLocationButton) {
    view.setShowsMyLocationButton(showMyLocationButton);
  }

  @ReactProp(name = "toolbarEnabled", defaultBoolean = true)
  public void setToolbarEnabled(MapView view, boolean toolbarEnabled) {
    view.setToolbarEnabled(toolbarEnabled);
  }

  // This is a private prop to improve performance of panDrag by disabling it when the callback
  // is not set
  @ReactProp(name = "handlePanDrag", defaultBoolean = false)
  public void setHandlePanDrag(MapView view, boolean handlePanDrag) {
    view.setHandlePanDrag(handlePanDrag);
  }

  @ReactProp(name = "showsTraffic", defaultBoolean = false)
  public void setShowTraffic(MapView view, boolean showTraffic) {
    view.map.setTrafficEnabled(showTraffic);
  }

  @ReactProp(name = "showsBuildings", defaultBoolean = false)
  public void setShowBuildings(MapView view, boolean showBuildings) {
    view.map.setBuildingsEnabled(showBuildings);
  }

  @ReactProp(name = "showsIndoors", defaultBoolean = false)
  public void setShowIndoors(MapView view, boolean showIndoors) {
    view.map.setIndoorEnabled(showIndoors);
  }

  @ReactProp(name = "showsIndoorLevelPicker", defaultBoolean = false)
  public void setShowsIndoorLevelPicker(MapView view, boolean showsIndoorLevelPicker) {
    view.map.getUiSettings().setIndoorLevelPickerEnabled(showsIndoorLevelPicker);
  }

  @ReactProp(name = "showsCompass", defaultBoolean = false)
  public void setShowsCompass(MapView view, boolean showsCompass) {
    view.map.getUiSettings().setCompassEnabled(showsCompass);
  }

  @ReactProp(name = "scrollEnabled", defaultBoolean = false)
  public void setScrollEnabled(MapView view, boolean scrollEnabled) {
    view.map.getUiSettings().setScrollGesturesEnabled(scrollEnabled);
  }

  @ReactProp(name = "zoomEnabled", defaultBoolean = false)
  public void setZoomEnabled(MapView view, boolean zoomEnabled) {
    view.map.getUiSettings().setZoomGesturesEnabled(zoomEnabled);
  }

  @ReactProp(name = "zoomControlEnabled", defaultBoolean = true)
  public void setZoomControlEnabled(MapView view, boolean zoomControlEnabled) {
    view.map.getUiSettings().setZoomControlsEnabled(zoomControlEnabled);
  }

  @ReactProp(name = "rotateEnabled", defaultBoolean = false)
  public void setRotateEnabled(MapView view, boolean rotateEnabled) {
    view.map.getUiSettings().setRotateGesturesEnabled(rotateEnabled);
  }

  @ReactProp(name = "scrollDuringRotateOrZoomEnabled", defaultBoolean = true)
  public void setScrollDuringRotateOrZoomEnabled(MapView view, boolean scrollDuringRotateOrZoomEnabled) {
    view.map.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(scrollDuringRotateOrZoomEnabled);
  }

  @ReactProp(name = "cacheEnabled", defaultBoolean = false)
  public void setCacheEnabled(MapView view, boolean cacheEnabled) {
    view.setCacheEnabled(cacheEnabled);
  }

  @ReactProp(name = "loadingEnabled", defaultBoolean = false)
  public void setLoadingEnabled(MapView view, boolean loadingEnabled) {
    view.enableMapLoading(loadingEnabled);
  }

  @ReactProp(name = "moveOnMarkerPress", defaultBoolean = true)
  public void setMoveOnMarkerPress(MapView view, boolean moveOnPress) {
    view.setMoveOnMarkerPress(moveOnPress);
  }

  @ReactProp(name = "loadingBackgroundColor", customType = "Color")
  public void setLoadingBackgroundColor(MapView view, @Nullable Integer loadingBackgroundColor) {
    view.setLoadingBackgroundColor(loadingBackgroundColor);
  }

  @ReactProp(name = "loadingIndicatorColor", customType = "Color")
  public void setLoadingIndicatorColor(MapView view, @Nullable Integer loadingIndicatorColor) {
    view.setLoadingIndicatorColor(loadingIndicatorColor);
  }

  @ReactProp(name = "pitchEnabled", defaultBoolean = false)
  public void setPitchEnabled(MapView view, boolean pitchEnabled) {
    view.map.getUiSettings().setTiltGesturesEnabled(pitchEnabled);
  }

  @ReactProp(name = "minZoomLevel")
  public void setMinZoomLevel(MapView view, float minZoomLevel) {
    view.map.setMinZoomPreference(minZoomLevel);
  }

  @ReactProp(name = "maxZoomLevel")
  public void setMaxZoomLevel(MapView view, float maxZoomLevel) {
    view.map.setMaxZoomPreference(maxZoomLevel);
  }

  @ReactProp(name = "kmlSrc")
  public void setKmlSrc(MapView view, String kmlUrl) {
    if (kmlUrl != null) {
      view.setKmlSrc(kmlUrl);
    }
  }

  @Override
  public void receiveCommand(@NonNull MapView view, String commandId, @Nullable ReadableArray args) {
    int duration;
    double lat;
    double lng;
    double lngDelta;
    double latDelta;
    ReadableMap region;
    ReadableMap camera;

    switch (commandId) {
      case "setCamera":
        if(args == null) {
          break;
        }
        camera = args.getMap(0);
        view.animateToCamera(camera, 0);
        break;

      case "animateCamera":
        if(args == null) {
          break;
        }
        camera = args.getMap(0);
        duration = args.getInt(1);
        view.animateToCamera(camera, duration);
        break;

      case "animateToRegion":
        if(args == null) {
          break;
        }
        region = args.getMap(0);
        duration = args.getInt(1);
        lng = region.getDouble("longitude");
        lat = region.getDouble("latitude");
        lngDelta = region.getDouble("longitudeDelta");
        latDelta = region.getDouble("latitudeDelta");
        LatLngBounds bounds = new LatLngBounds(
            new LatLng(lat - latDelta / 2, lng - lngDelta / 2), // southwest
            new LatLng(lat + latDelta / 2, lng + lngDelta / 2)  // northeast
        );
        view.animateToRegion(bounds, duration);
        break;

      case "fitToElements":
        if(args == null) {
          break;
        }
        view.fitToElements(args.getMap(0), args.getBoolean(1));
        break;

      case "fitToSuppliedMarkers":
        if(args == null) {
          break;
        }
        view.fitToSuppliedMarkers(args.getArray(0), args.getMap(1), args.getBoolean(2));
        break;

      case "fitToCoordinates":
        if(args == null) {
          break;
        }
        view.fitToCoordinates(args.getArray(0), args.getMap(1), args.getBoolean(2));
        break;

      case "setMapBoundaries":
        if(args == null) {
          break;
        }
        view.setMapBoundaries(args.getMap(0), args.getMap(1));
        break;

      case "setIndoorActiveLevelIndex":
        if(args == null) {
          break;
        }
        view.setIndoorActiveLevelIndex(args.getInt(0));
        break;
    }
  }

  @Override
  @Nullable
  public Map getExportedCustomDirectEventTypeConstants() {
    Map<String, Object> baseEventTypeConstants =
        super.getExportedCustomBubblingEventTypeConstants();
    Map<String, Object> eventTypeConstants = new HashMap<String, Object>();
    eventTypeConstants.putAll(
         MapBuilder.<String, Object>builder()
            .put(
                "topOnMapReady",
                MapBuilder.of("registrationName", "onMapReady"))
            .put(
                "topOnPress",
                MapBuilder.of("registrationName", "onPress"))
            .put(
                "topOnLongPress",
                MapBuilder.of("registrationName", "onLongPress"))
            .put(
                "topOnMarkerPress",
                MapBuilder.of("registrationName", "onMarkerPress"))
            .put(
                "topOnMarkerSelect",
                MapBuilder.of("registrationName", "onMarkerSelect"))
            .put(
                "topOnMarkerDeselect",
                MapBuilder.of("registrationName", "onMarkerDeselect"))
            .put(
                "topOnCalloutPress",
                MapBuilder.of("registrationName", "onCalloutPress"))
            .put(
                "topOnUserLocationChange",
                MapBuilder.of("registrationName", "onUserLocationChange"))
            .put(
                "topOnMarkerDragStart",
                MapBuilder.of("registrationName", "onMarkerDragStart"))
            .put(
                "topOnMarkerDrag",
                MapBuilder.of("registrationName", "onMarkerDrag"))
            .put(
                "topOnMarkerDragEnd",
                MapBuilder.of("registrationName", "onMarkerDragEnd"))
            .put(
                "topOnPanDrag",
                MapBuilder.of("registrationName", "onPanDrag"))
            .put(
                "topOnKmlReady",
                MapBuilder.of("registrationName", "onKmlReady"))
            .put(
                "topOnPoiClick",
                MapBuilder.of("registrationName", "onPoiClick"))
            .put(
                "topOnIndoorLevelActivated",
                MapBuilder.of("registrationName", "onIndoorLevelActivated"))
            .put(
                "topOnIndoorBuildingFocused",
                MapBuilder.of("registrationName", "onIndoorBuildingFocused"))
            .put(
                "topOnDoublePress",
                MapBuilder.of("registrationName", "onDoublePress"))
            .put(
                "topOnMapLoaded",
                MapBuilder.of("registrationName", "onMapLoaded"))
            .build());
    return eventTypeConstants;
  }





  //       "topOnMapReady", MapBuilder.of("registrationName", "onMapReady"),
  //       "topOnPress", MapBuilder.of("registrationName", "onPress"),
  //       "topOnLongPress", MapBuilder.of("registrationName", "onLongPress"),
  //       "topOnMarkerPress", MapBuilder.of("registrationName", "onMarkerPress"),
  //       "topOnMarkerSelect", MapBuilder.of("registrationName", "onMarkerSelect"),
  //       "topOnMarkerDeselect", MapBuilder.of("registrationName", "onMarkerDeselect"),
  //       "topOnCalloutPress", MapBuilder.of("registrationName", "onCalloutPress")
  //   );

  //   map.putAll(MapBuilder.of(
  //       "topOnUserLocationChange", MapBuilder.of("registrationName", "onUserLocationChange"),
  //       "topOnMarkerDragStart", MapBuilder.of("registrationName", "onMarkerDragStart"),
  //       "topOnMarkerDrag", MapBuilder.of("registrationName", "onMarkerDrag"),
  //       "topOnMarkerDragEnd", MapBuilder.of("registrationName", "onMarkerDragEnd"),
  //       "topOnPanDrag", MapBuilder.of("registrationName", "onPanDrag"),
  //       "topOnKmlReady", MapBuilder.of("registrationName", "onKmlReady"),
  //       "topOnPoiClick", MapBuilder.of("registrationName", "onPoiClick")
  //   ));

  //   map.putAll(MapBuilder.of(
  //       "topOnIndoorLevelActivated", MapBuilder.of("registrationName", "onIndoorLevelActivated"),
  //       "topOnIndoorBuildingFocused", MapBuilder.of("registrationName", "onIndoorBuildingFocused"),
  //       "topOnDoublePress", MapBuilder.of("registrationName", "onDoublePress"),
  //       "topOnMapLoaded", MapBuilder.of("registrationName", "onMapLoaded")
  //   ));

  //   return map;
  // }

  @Override
  public LayoutShadowNode createShadowNodeInstance() {
    // A custom shadow node is needed in order to pass back the width/height of the map to the
    // view manager so that it can start applying camera moves with bounds.
    return new SizeReportingShadowNode();
  }

  @Override
  public void addView(MapView parent, View child, int index) {
    Log.d("SHIT", "MapView addView");
    parent.addFeature(child, index);
  }

  @Override
  public int getChildCount(MapView view) {
    return view.getFeatureCount();
  }

  @Override
  public View getChildAt(MapView view, int index) {
    return view.getFeatureAt(index);
  }

  @Override
  public void removeViewAt(MapView parent, int index) {
    parent.removeFeatureAt(index);
  }

  @Override
  public void updateExtraData(MapView view, Object extraData) {
    view.updateExtraData(extraData);
  }

  void pushEvent(ThemedReactContext context, View view, String name, WritableMap data) {
    Log.d("SHIT", "pushEvent " + name);
    Log.d("SHIT", "context" + context.getModuleName());
    // Log.d("SHIT", "context.getJSModuleName(RCTEventEmitter.class " + context.getJSModule(RCTModernEventEmitter.class));
    // Log.d("SHIT", "context.getJSModuleName(RCTEventEmitter.class " + context.getJSModule(ReactEventEmitter.class));
    // context.getJSModule(RCTEventEmitter.class)
    //     .receiveEvent(view.getId(), name, data);

    int viewId = view.getId();

    EventDispatcher eventDispatcher = UIManagerHelper.getEventDispatcherForReactTag(context, viewId);
    Log.d("SHIT", "eventDispatcher " + eventDispatcher);
    if (eventDispatcher != null) {
    switch (name):
      case "topMapReady"
        eventDispatcher.dispatchEvent(new MapReadyEvent(UIManagerHelper.getSurfaceId(context), viewId, "test"));
        break;
      case "topPress"
        eventDispatcher.dispatchEvent(new PressEvent(UIManagerHelper.getSurfaceId(context), viewId, "test"));
        break;
      case "topLongPress"
        eventDispatcher.dispatchEvent(new LongPressEvent(UIManagerHelper.getSurfaceId(context), viewId, "test"));
        break;
      case "topMarkerPress"
        eventDispatcher.dispatchEvent(new MarkerPressEvent(UIManagerHelper.getSurfaceId(context), viewId, "test"));
        break;
      case "topMarkerSelect"
        eventDispatcher.dispatchEvent(new MarkerSelectEvent(UIManagerHelper.getSurfaceId(context), viewId, "test"));
        break;
      case "topMarkerDeselect"
        eventDispatcher.dispatchEvent(new MarkerDeselectEvent(UIManagerHelper.getSurfaceId(context), viewId, "test"));
        break;
      case "topCalloutPress"
        eventDispatcher.dispatchEvent(new CalloutPressEvent(UIManagerHelper.getSurfaceId(context), viewId, "test"));
        break;
      case "topUserLocationChange"
        eventDispatcher.dispatchEvent(new UserLocationChangeEvent(UIManagerHelper.getSurfaceId(context), viewId, "test"));
        break;
      case "topMarkerDragStart"
        eventDispatcher.dispatchEvent(new MarkerDragStartEvent(UIManagerHelper.getSurfaceId(context), viewId, "test"));
        break;
      case "topMarkerDrag"
        eventDispatcher.dispatchEvent(new MarkerDragEvent(UIManagerHelper.getSurfaceId(context), viewId, "test"));
        break;
      case "topMarkerDragEnd"
        eventDispatcher.dispatchEvent(new MarkerDragEndEvent(UIManagerHelper.getSurfaceId(context), viewId, "test"));
        break;
      case "topPanDrag"
        eventDispatcher.dispatchEvent(new PanDragEvent(UIManagerHelper.getSurfaceId(context), viewId, "test"));
        break;
      case "topKmlReady"
        eventDispatcher.dispatchEvent(new KmlReadyEvent(UIManagerHelper.getSurfaceId(context), viewId, "test"));
        break;
      case "topPoiClick"
        eventDispatcher.dispatchEvent(new PoiClickEvent(UIManagerHelper.getSurfaceId(context), viewId, "test"));
        break;
      case "topIndoorLevelActivated"
        eventDispatcher.dispatchEvent(new IndoorLevelActivatedEvent(UIManagerHelper.getSurfaceId(context), viewId, "test"));
        break;
      case "topIndoorBuildingFocused"
        eventDispatcher.dispatchEvent(new IndoorBuildingFocusedEvent(UIManagerHelper.getSurfaceId(context), viewId, "test"));
        break;
      case "topDoublePress"
        eventDispatcher.dispatchEvent(new DoublePressEvent(UIManagerHelper.getSurfaceId(context), viewId, "test"));
        break;
      case "topMapLoaded"
        eventDispatcher.dispatchEvent(new MapLoadedEvent(UIManagerHelper.getSurfaceId(context), viewId, "test"));
        break;
    }
  }

  @Override
  public void onDropViewInstance(MapView view) {
    view.doDestroy();
    super.onDropViewInstance(view);
  }

}
