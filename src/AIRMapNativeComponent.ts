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
    onMapReady?: BubblingEventHandler<LogEvent>;

}

export default codegenNativeComponent<NativeProps>(
  'AIRMap',
) as HostComponent<NativeProps>;