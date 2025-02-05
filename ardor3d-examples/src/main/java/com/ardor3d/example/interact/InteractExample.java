/**
 * Copyright (c) 2008-2021 Bird Dog Games, Inc.
 *
 * This file is part of Ardor3D.
 *
 * Ardor3D is free software: you can redistribute it and/or modify it
 * under the terms of its license which may be found in the accompanying
 * LICENSE file or at <https://git.io/fjRmv>.
 */

package com.ardor3d.example.interact;

import java.net.URISyntaxException;

import com.ardor3d.bounding.BoundingBox;
import com.ardor3d.bounding.BoundingSphere;
import com.ardor3d.example.ExampleBase;
import com.ardor3d.example.Purpose;
import com.ardor3d.extension.interact.InteractManager;
import com.ardor3d.extension.interact.filter.AllowScaleFilter;
import com.ardor3d.extension.interact.filter.MinMaxScaleFilter;
import com.ardor3d.extension.interact.filter.PlaneBoundaryFilter;
import com.ardor3d.extension.interact.widget.AbstractInteractWidget;
import com.ardor3d.extension.interact.widget.InteractMatrix;
import com.ardor3d.extension.interact.widget.MoveMultiPlanarWidget;
import com.ardor3d.extension.interact.widget.MovePlanarWidget;
import com.ardor3d.extension.interact.widget.MoveWidget;
import com.ardor3d.extension.interact.widget.RotateWidget;
import com.ardor3d.extension.interact.widget.SimpleScaleWidget;
import com.ardor3d.image.Image;
import com.ardor3d.image.Texture;
import com.ardor3d.image.Texture2D;
import com.ardor3d.input.keyboard.Key;
import com.ardor3d.input.logical.InputTrigger;
import com.ardor3d.input.logical.KeyHeldCondition;
import com.ardor3d.input.logical.KeyPressedCondition;
import com.ardor3d.input.logical.KeyReleasedCondition;
import com.ardor3d.input.mouse.MouseCursor;
import com.ardor3d.intersection.PickData;
import com.ardor3d.intersection.Pickable;
import com.ardor3d.intersection.PrimitivePickResults;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Plane;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.Camera;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.hint.PickingHint;
import com.ardor3d.scenegraph.shape.Box;
import com.ardor3d.scenegraph.shape.Sphere;
import com.ardor3d.util.MaterialUtil;
import com.ardor3d.util.ReadOnlyTimer;
import com.ardor3d.util.TextureManager;
import com.ardor3d.util.resource.ResourceLocatorTool;
import com.ardor3d.util.resource.SimpleResourceLocator;

/**
 * An example illustrating the use of the interact framework.
 */
@Purpose(
    htmlDescriptionKey = "com.ardor3d.example.interact.InteractExample", //
    thumbnailPath = "com/ardor3d/example/media/thumbnails/interact_InteractExample.jpg", //
    maxHeapMemory = 64)
public class InteractExample extends ExampleBase {

  private InteractManager manager;
  private RotateWidget rotateWidget;
  private MoveWidget moveWidget;
  private SimpleScaleWidget scaleWidget;

  public static void main(final String[] args) {
    start(InteractExample.class);
  }

  @Override
  protected void updateExample(final ReadOnlyTimer timer) {
    manager.update(timer);
  }

  @Override
  protected void renderExample(final Renderer renderer) {
    super.renderExample(renderer);
    manager.render(renderer);
  }

  @Override
  protected void initExample() {
    _canvas.setTitle("Interact Example");

    final Camera camera = _canvas.getCanvasRenderer().getCamera();
    camera.setLocation(-20, 70, 180);
    camera.lookAt(0, 0, 0, Vector3.UNIT_Y);

    // setup our interact controls
    addControls();

    // create a floor to act as a reference.
    addFloor();

    // create an object or two to manipulate
    addObjects();

    MaterialUtil.autoMaterials(_root);
  }

  private void addFloor() {
    final Box floor = new Box("floor", Vector3.ZERO, 100, 5, 100);
    floor.setTranslation(0, -5, 0);
    final TextureState ts = new TextureState();
    ts.setTexture(TextureManager.load("models/obj/pitcher.jpg", Texture.MinificationFilter.Trilinear, true));
    floor.setRenderState(ts);
    floor.getSceneHints().setPickingHint(PickingHint.Pickable, false);
    floor.setModelBound(new BoundingBox());
    _root.attachChild(floor);
  }

  private void addObjects() {
    final Box box1 = new Box("box", Vector3.ZERO, 5, 15, 5);
    box1.setTranslation(0, box1.getYExtent(), 0);
    TextureState ts = new TextureState();
    ts.setTexture(TextureManager.load("images/skybox/1.jpg", Texture.MinificationFilter.Trilinear, true));
    box1.setRenderState(ts);
    box1.getSceneHints().setPickingHint(PickingHint.Pickable, true);
    box1.setModelBound(new BoundingBox());

    final Node base = new Node();
    base.setTranslation(0, 0, 0);
    base.attachChild(box1);
    _root.attachChild(base);

    final Sphere sphere = new Sphere("sphere", Vector3.ZERO, 16, 16, 8);
    ts = new TextureState();
    ts.setTexture(TextureManager.load("images/water/dudvmap.png", Texture.MinificationFilter.Trilinear, true));
    sphere.setRenderState(ts);
    sphere.getSceneHints().setPickingHint(PickingHint.Pickable, true);
    sphere.setModelBound(new BoundingSphere());

    final Node joint = new Node();
    joint.setTranslation(0, sphere.getRadius() + 2 * box1.getYExtent(), 0);
    joint.attachChild(sphere);
    base.attachChild(joint);

    final Box box2 = new Box("box", Vector3.ZERO, 5, 15, 5);
    box2.setTranslation(0, box2.getYExtent(), 0);
    ts = new TextureState();
    ts.setTexture(TextureManager.load("images/skybox/3.jpg", Texture.MinificationFilter.Trilinear, true));
    box2.setRenderState(ts);
    box2.getSceneHints().setPickingHint(PickingHint.Pickable, true);
    box2.setModelBound(new BoundingBox());

    final Node arm = new Node();
    arm.setTranslation(0, sphere.getRadius(), 0);
    arm.attachChild(box2);
    joint.attachChild(arm);

    // auto select the joint
    _root.updateGeometricState(0);
    manager.setSpatialTarget(joint);
  }

  @Override
  protected void updateLogicalLayer(final ReadOnlyTimer timer) {
    manager.getLogicalLayer().checkTriggers(timer.getTimePerFrame());
  }

  private void addControls() {
    setupCursors();

    // create our manager
    manager = new InteractManager();
    manager.setupInput(_canvas, _physicalLayer, _logicalLayer);

    // add some widgets.
    rotateWidget = new RotateWidget().withXAxis().withYAxis().withZAxis();
    rotateWidget
        .setTexture((Texture2D) TextureManager.load("images/tick.png", Texture.MinificationFilter.Trilinear, true));
    manager.addWidget(rotateWidget);

    scaleWidget = new SimpleScaleWidget().withArrow(Vector3.UNIT_Y);
    manager.addWidget(scaleWidget);

    moveWidget = new MoveWidget().withXAxis().withYAxis().withZAxis();
    manager.addWidget(moveWidget);

    // set the default as current
    manager.setActiveWidget(rotateWidget);

    // add triggers to change which widget is active
    manager.getLogicalLayer().registerTrigger(new InputTrigger(new KeyHeldCondition(Key.LEFT_SHIFT),
        (source, inputStates, tpf) -> manager.setActiveWidget(scaleWidget)));
    manager.getLogicalLayer().registerTrigger(new InputTrigger(new KeyReleasedCondition(Key.LEFT_SHIFT),
        (source, inputStates, tpf) -> manager.setActiveWidget(rotateWidget)));
    manager.getLogicalLayer().registerTrigger(new InputTrigger(new KeyHeldCondition(Key.LEFT_CONTROL),
        (source, inputStates, tpf) -> manager.setActiveWidget(moveWidget)));
    manager.getLogicalLayer().registerTrigger(new InputTrigger(new KeyReleasedCondition(Key.LEFT_CONTROL),
        (source, inputStates, tpf) -> manager.setActiveWidget(rotateWidget)));

    // add toggle for matrix mode on widgets.
    manager.getLogicalLayer()
        .registerTrigger(new InputTrigger(new KeyPressedCondition(Key.R), (source, inputStates, tpf) -> {
          rotateWidget.setInteractMatrix(
              rotateWidget.getInteractMatrix() == InteractMatrix.World ? InteractMatrix.Local : InteractMatrix.World);
          rotateWidget.targetDataUpdated(manager);
          moveWidget.setInteractMatrix(rotateWidget.getInteractMatrix());
          moveWidget.targetDataUpdated(manager);
        }));

    // add triggers to change which widget is active
    manager.getLogicalLayer()
        .registerTrigger(new InputTrigger(new KeyPressedCondition(Key.SPACE), (source, inputStates, tpf) -> {
          manager.getSpatialTarget().setRotation(Matrix3.IDENTITY);
          manager.fireTargetDataUpdated();
        }));

    // add some filters
    scaleWidget.addFilter(new MinMaxScaleFilter(1.0, 10.0));
    scaleWidget.addFilter(new AllowScaleFilter(false, true, false));
    moveWidget.addFilter(new PlaneBoundaryFilter(new Plane(Vector3.UNIT_Y, 0)));
  }

  public static void setupCursors() {
    try {
      final SimpleResourceLocator srl = new SimpleResourceLocator(ResourceLocatorTool
          .getClassPathResource(AbstractInteractWidget.class, "com/ardor3d/extension/interact/widget/"));
      ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, srl);

      // ROTATE
      Image img = TextureManager.load("rotate.png", Texture.MinificationFilter.BilinearNoMipMaps, true).getImage();
      RotateWidget.DEFAULT_CURSOR = new MouseCursor("rotate", img, img.getWidth() / 2, img.getHeight() / 2);

      // SCALE
      img = TextureManager.load("scale.png", Texture.MinificationFilter.BilinearNoMipMaps, true).getImage();
      SimpleScaleWidget.DEFAULT_CURSOR = new MouseCursor("scale", img, 3, img.getHeight() - 3);

      // MOVE
      img = TextureManager.load("move.png", Texture.MinificationFilter.BilinearNoMipMaps, true).getImage();
      MoveWidget.DEFAULT_CURSOR = new MouseCursor("move", img, img.getWidth() / 2, img.getHeight() / 2);
      MoveMultiPlanarWidget.DEFAULT_CURSOR = new MouseCursor("move", img, img.getWidth() / 2, img.getHeight() / 2);
      MovePlanarWidget.DEFAULT_CURSOR = new MouseCursor("move", img, img.getWidth() / 2, img.getHeight() / 2);
    } catch (final URISyntaxException ex) {
      ex.printStackTrace();
    }
  }

  @Override
  protected void processPicks(final PrimitivePickResults pickResults) {
    final PickData pick = pickResults.findFirstIntersectingPickData();
    if (pick != null) {
      final Pickable target = pick.getTarget();
      if (target instanceof Spatial) {
        manager.setSpatialTarget(((Spatial) target).getParent());
        return;
      }
    }
    manager.setSpatialTarget(null);
  }

}
