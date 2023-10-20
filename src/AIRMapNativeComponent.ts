import type {ViewProps} from 'ViewPropTypes';
import type {
  Float,
  Double,
  Int32,
  BubblingEventHandler,
} from 'react-native/Libraries/Types/CodegenTypes';
import type {HostComponent} from 'react-native';
import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';

type LogEvent = {
    target: Int32,
    message: string,
};

export interface NativeProps extends ViewProps {
  // add other props here
    onMapReady?:  BubblingEventHandler<LogEvent>;
    onPress?:  BubblingEventHandler<LogEvent>;
    onLongPress?:  BubblingEventHandler<LogEvent>;
    onMarkerPress?:  BubblingEventHandler<LogEvent>;
    onMarkerSelect?:  BubblingEventHandler<LogEvent>;
    onMarkerDeselect?:  BubblingEventHandler<LogEvent>;
    onCalloutPress?:  BubblingEventHandler<LogEvent>;
    onUserLocationChange?:  BubblingEventHandler<LogEvent>;
    onMarkerDragStart?:  BubblingEventHandler<LogEvent>;
    onMarkerDrag?:  BubblingEventHandler<LogEvent>;
    onMarkerDragEnd?:  BubblingEventHandler<LogEvent>;
    onPanDrag?:  BubblingEventHandler<LogEvent>;
    onKmlReady?:  BubblingEventHandler<LogEvent>;
    onPoiClick?:  BubblingEventHandler<LogEvent>;
    onIndoorLevelActivated?:  BubblingEventHandler<LogEvent>;
    onIndoorBuildingFocused?:  BubblingEventHandler<LogEvent>;
    onDoublePress?:  BubblingEventHandler<LogEvent>;
    onMapLoaded?:  BubblingEventHandler<LogEvent>;

}

export default codegenNativeComponent<NativeProps>(
  'AIRMap',
) as HostComponent<NativeProps>;