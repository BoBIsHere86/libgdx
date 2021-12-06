/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx.maps;

import com.badlogic.gdx.utils.GdxRuntimeException;

/** Map layer containing a set of objects and properties */
public class MapLayer {
	private String name = "";
	private float opacity = 1.0f;
	private boolean visible = true;
	private float offsetX;
	private float offsetY;
	private float renderOffsetX;
	private float renderOffsetY;
	private float parallaxX;
	private float parallaxY;
	private boolean renderOffsetDirty = true;
	private MapLayer parent;
	private MapObjects objects = new MapObjects();
	private MapProperties properties = new MapProperties();

	/** @return layer's name */
	public String getName () {
		return name;
	}

	/** @param name new name for the layer */
	public void setName (String name) {
		this.name = name;
	}

	/** @return layer's opacity */
	public float getOpacity () {
		return opacity;
	}

	/** @param opacity new opacity for the layer */
	public void setOpacity (float opacity) {
		this.opacity = opacity;
	}

	/** @return layer's x offset */
	public float getOffsetX () {
		return offsetX;
	}

	/** @param offsetX new x offset for the layer */
	public void setOffsetX (float offsetX) {
		this.offsetX = offsetX;
		invalidateRenderOffset();
	}

	/** @return layer's y offset */
	public float getOffsetY () {
		return offsetY;
	}

	/** @param offsetY new y offset for the layer */
	public void setOffsetY (float offsetY) {
		this.offsetY = offsetY;
		invalidateRenderOffset();
	}

	/** @return the layer's x render offset, this takes into consideration all parent layers' offsets and its parallax scrolling factor */
	public float getRenderOffsetX(float viewX, float unitScale) {
		if (renderOffsetDirty) calculateRenderOffsets();
		return renderOffsetX * unitScale + viewX * (parallaxX - 1);
	}

	/** @return the layer's y render offset, this takes into consideration all parent layers' offsets and its parallax scrolling factor */
	public float getRenderOffsetY(float viewY, float unitScale) {
		if (renderOffsetDirty) calculateRenderOffsets();
		return renderOffsetY * unitScale + viewY * (parallaxY - 1);
	}

	public float getParallaxX() {
		return parallaxX;
	}

	public void setParallaxX(float parallaxX) {
		this.parallaxX = parallaxX;
	}

	public float getParallaxY() {
		return parallaxY;
	}

	public void setParallaxY(float parallaxY) {
		this.parallaxY = parallaxY;
	}

	public boolean hasParallaxScrolling() {
		return parallaxX != 1f || parallaxY != 1f;
	}

	/** set the renderOffsetDirty state to true, when this layer or any parents' offset has changed **/
	public void invalidateRenderOffset () {
		renderOffsetDirty = true;
	}

	/** @return the layer's parent {@link MapLayer}, or null if the layer does not have a parent **/
	public MapLayer getParent () {
		return parent;
	}

	/** @param parent the layer's new parent {@MapLayer}, internal use only **/
	public void setParent (MapLayer parent) {
		if (parent == this) throw new GdxRuntimeException("Can't set self as the parent");
		this.parent = parent;

		// in Tiled the final parallax scrolling factor of a layer is multiplied
		// by the value of each parent layer
		parallaxX *= parent.getParallaxX();
		parallaxY *= parent.getParallaxY();
	}

	/** @return collection of objects contained in the layer */
	public MapObjects getObjects () {
		return objects;
	}

	/** @return whether the layer is visible or not */
	public boolean isVisible () {
		return visible;
	}

	/** @param visible toggles layer's visibility */
	public void setVisible (boolean visible) {
		this.visible = visible;
	}

	/** @return layer's set of properties */
	public MapProperties getProperties () {
		return properties;
	}

	protected void calculateRenderOffsets() {
		if (parent != null) {
			parent.calculateRenderOffsets();
			// get parent's render offset without parallax scrolling offset and without scaling
			renderOffsetX = parent.getRenderOffsetX(0f, 1f) + offsetX;
			renderOffsetY = parent.getRenderOffsetY(0f, 1f) + offsetY;
		} else {
			renderOffsetX = offsetX;
			renderOffsetY = offsetY;
		}
		renderOffsetDirty = false;
	}
}
