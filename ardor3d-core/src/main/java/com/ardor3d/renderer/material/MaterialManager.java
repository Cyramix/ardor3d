/**
 * Copyright (c) 2008-2018 Bird Dog Games, Inc.
 *
 * This file is part of Ardor3D.
 *
 * Ardor3D is free software: you can redistribute it and/or modify it
 * under the terms of its license which may be found in the accompanying
 * LICENSE file or at <http://www.ardor3d.com/LICENSE>.
 */

package com.ardor3d.renderer.material;

import java.util.List;

import com.ardor3d.scenegraph.Mesh;

public enum MaterialManager {

    INSTANCE;

    private RenderMaterial _defaultMaterial = null;

    public void setDefaultMaterial(final RenderMaterial material) {
        _defaultMaterial = material;
    }

    public RenderMaterial getDefaultMaterial() {
        return _defaultMaterial;
    }

    public MaterialTechnique chooseTechnique(final Mesh mesh) {

        // find the local or inherited material for the given mesh
        RenderMaterial material = mesh.getWorldRenderMaterial();

        // if we have no material, use any set default
        material = _defaultMaterial;

        // look for the technique to use in the material
        return chooseTechnique(mesh, material);
    }

    private MaterialTechnique chooseTechnique(final Mesh mesh, final RenderMaterial material) {
        if (material == null || material.getTechniques().isEmpty()) {
            return null;
        }

        // pull out our techniques
        final List<MaterialTechnique> techniques = material.getTechniques();

        // if we have just one technique, return that
        if (techniques.size() == 1) {
            return techniques.get(0);
        }

        MaterialTechnique best = null;
        int bestScore = Integer.MIN_VALUE;
        for (int i = 0; i < techniques.size(); i++) {
            final MaterialTechnique tech = techniques.get(i);
            final int score = tech.getScore(mesh);
            if (score > bestScore) {
                best = tech;
                bestScore = score;
            }
        }

        return best;

    }

}