// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the GPLv3 license.
package org.openstreetmap.josm.plugins.streetside.utils;

import java.util.UUID;

/**
 * 
 */
public class StreetsideSequenceIdGenerator {
  
  public static String generateId() {
    
    return UUID.randomUUID().toString();
    
  }

}
