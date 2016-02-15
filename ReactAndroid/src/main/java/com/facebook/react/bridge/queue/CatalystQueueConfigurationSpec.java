/**
 * Copyright (c) 2015-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.facebook.react.bridge.queue;

import android.text.TextUtils;

import javax.annotation.Nullable;

import com.facebook.infer.annotation.Assertions;

/**
 * Spec for creating a CatalystQueueConfiguration. This exists so that CatalystInstance is able to
 * set Exception handlers on the MessageQueueThreads it uses and it would not be super clean if the
 * threads were configured, then passed to CatalystInstance where they are configured more. These
 * specs allows the Threads to be created fully configured.
 */
public class CatalystQueueConfigurationSpec {

  private final MessageQueueThreadSpec mNativeModulesQueueThreadSpec;
  private final MessageQueueThreadSpec mJSQueueThreadSpec;

  private CatalystQueueConfigurationSpec(
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

  public static CatalystQueueConfigurationSpec createDefault(String serverDomain, String serverPort) {
    return builder()
        .setJSQueueThreadSpec(MessageQueueThreadSpec.newBackgroundThreadSpec(
                generateThreadName("js", serverDomain, serverPort)))
        .setNativeModulesQueueThreadSpec(MessageQueueThreadSpec.newBackgroundThreadSpec(
                generateThreadName("native_modules", serverDomain, serverPort)))

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

    public CatalystQueueConfigurationSpec build() {
      return new CatalystQueueConfigurationSpec(
          Assertions.assertNotNull(mNativeModulesQueueSpec),
          Assertions.assertNotNull(mJSQueueSpec));
    }
  }
}
