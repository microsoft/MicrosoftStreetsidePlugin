// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the GPLv3 license.
package org.openstreetmap.josm.plugins.streetside.utils;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import javax.json.Json;
import javax.json.JsonObject;

public final class JsonUtil {
  private JsonUtil() {
    // Private constructor to avoid instantiation
  }

  public static JsonObject string2jsonObject(String s) {
    return Json.createReader(new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8))).readObject();
  }
}
