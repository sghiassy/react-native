/**
 * Copyright (c) 2015-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.facebook.react.bridge.queue;

import android.os.Build;
import android.text.TextUtils;

import com.facebook.infer.annotation.Assertions;

import javax.annotation.Nullable;

/**
 * Spec for creating a ReactQueueConfiguration. This exists so that CatalystInstance is able to
 * set Exception handlers on the MessageQueueThreads it uses and it would not be super clean if the
 * threads were configured, then passed to CatalystInstance where they are configured more. These
 * specs allows the Threads to be created fully configured.
 */
public class ReactQueueConfigurationSpec {

  private static final long LEGACY_STACK_SIZE_BYTES = 2000000;

  private final MessageQueueThreadSpec mNativeModulesQueueThreadSpec;
  private final MessageQueueThreadSpec mJSQueueThreadSpec;

  private ReactQueueConfigurationSpec(
      MessageQueueThreadSpec nativeModulesQueueThreadSpec,
      MessageQueueThreadSpec jsQueueThreadSpec) {
    mNativeModulesQueueThreadSpec = nativeModulesQueueThreadSpec;
    mJSQueueThreadSpec = jsQueueThreadSpec;
  }

  public MessageQueueThreadSpec getNativeModulesQueueThreadSpec() {
    return mNativeModulesQueueThreadSpec;
  }

  public MessageQueueThreadSpec getJSQueueThreadSpec() {
    return mJSQueueThreadSpec;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static ReactQueueConfigurationSpec createDefault(String serverDomain, String serverPort) {
    String nativeModulesThreadName = generateThreadName("native_modules", serverDomain, serverPort);
    String jsThreadName = generateThreadName("js", serverDomain, serverPort);

    MessageQueueThreadSpec spec = Build.VERSION.SDK_INT < 21 ?
      MessageQueueThreadSpec.newBackgroundThreadSpec(nativeModulesThreadName, LEGACY_STACK_SIZE_BYTES) :
      MessageQueueThreadSpec.newBackgroundThreadSpec(nativeModulesThreadName);
    return builder()
        .setJSQueueThreadSpec(MessageQueueThreadSpec.newBackgroundThreadSpec(jsThreadName))
        .setNativeModulesQueueThreadSpec(spec)
        .build();
  }

  private static String generateThreadName(String threadType, String serverDomain, String serverPort) {
    StringBuilder stringBuilder = new StringBuilder(threadType);
    if (!TextUtils.isEmpty(serverDomain)) {
      stringBuilder.append("_").append(serverDomain);
    }
    if (!TextUtils.isEmpty(serverPort)) {
      stringBuilder.append(":").append(serverPort);
    }
    return stringBuilder.toString();
  }

  public static class Builder {

    private @Nullable MessageQueueThreadSpec mNativeModulesQueueSpec;
    private @Nullable MessageQueueThreadSpec mJSQueueSpec;

    public Builder setNativeModulesQueueThreadSpec(MessageQueueThreadSpec spec) {
      Assertions.assertCondition(
          mNativeModulesQueueSpec == null,
          "Setting native modules queue spec multiple times!");
      mNativeModulesQueueSpec = spec;
      return this;
    }

    public Builder setJSQueueThreadSpec(MessageQueueThreadSpec spec) {
      Assertions.assertCondition(mJSQueueSpec == null, "Setting JS queue multiple times!");
      mJSQueueSpec = spec;
      return this;
    }

    public ReactQueueConfigurationSpec build() {
      return new ReactQueueConfigurationSpec(
          Assertions.assertNotNull(mNativeModulesQueueSpec),
          Assertions.assertNotNull(mJSQueueSpec));
    }
  }
}
