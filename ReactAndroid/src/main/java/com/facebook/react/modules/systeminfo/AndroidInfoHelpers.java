package com.facebook.react.modules.systeminfo;

import android.os.Build;
import android.text.TextUtils;

public class AndroidInfoHelpers {

  public static final String EMULATOR_LOCALHOST = "10.0.2.2:%s";
  public static final String GENYMOTION_LOCALHOST = "10.0.3.2:%s";
  public static final String DEVICE_LOCALHOST = "localhost:%s";

  public static final String DEFAULT_PORT = "8081";

  private static boolean isRunningOnGenymotion() {
    return Build.FINGERPRINT.contains("vbox");
  }

  private static boolean isRunningOnStockEmulator() {
    return Build.FINGERPRINT.contains("generic");
  }

  public static String getServerHost(String mJSServerPort) {
    final String serverPort = TextUtils.isEmpty(mJSServerPort)
            ? DEFAULT_PORT
            : mJSServerPort;

    // Since genymotion runs in vbox it use different hostname to refer to adb host.
    // We detect whether app runs on genymotion and replace js bundle server hostname accordingly

    if (isRunningOnGenymotion()) {
      return String.format(GENYMOTION_LOCALHOST, serverPort);
    }

    if (isRunningOnStockEmulator()) {
      return String.format(EMULATOR_LOCALHOST, serverPort);
    }

    return String.format(DEVICE_LOCALHOST, serverPort);
  }
}
