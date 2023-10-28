import type {ViewProps} from 'ViewPropTypes';
import type {
  Float,
  Double,
  Int32,
  BubblingEventHandler,
} from 'react-native/Libraries/Types/CodegenTypes';
import type {HostComponent} from 'react-native';
import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';

export interface NativeProps extends ViewProps {
  // add other props here
   
}

export default codegenNativeComponent<NativeProps>(
  'AIRMapMarker',
) as HostComponent<NativeProps>;