/**
 * Copyright (c) 2008-2020 Bird Dog Games, Inc.
 *
 * This file is part of Ardor3D.
 *
 * Ardor3D is free software: you can redistribute it and/or modify it
 * under the terms of its license which may be found in the accompanying
 * LICENSE file or at <https://git.io/fjRmv>.
 */

package com.ardor3d.extension.shadow.map;

import com.ardor3d.math.Vector3;

public class MockPSSMCamera extends PSSMCamera {
  public Vector3 getExtents() { return _extents; }
}
