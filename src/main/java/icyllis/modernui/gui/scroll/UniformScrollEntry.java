/*
 * Modern UI.
 * Copyright (C) 2019 BloCamLimb. All rights reserved.
 *
 * Modern UI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Modern UI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Modern UI. If not, see <https://www.gnu.org/licenses/>.
 */

package icyllis.modernui.gui.scroll;

import icyllis.modernui.gui.master.Widget;

import javax.annotation.Nonnull;

/**
 * Entry in uniform scroll group with same height
 */
public abstract class UniformScrollEntry extends Widget {

    protected final ScrollWindow<?> window;

    protected float centerX;

    public UniformScrollEntry(@Nonnull ScrollWindow<?> window, float height) {
        super(window.getModule());
        this.window = window;
        this.height = height;
    }

    @Override
    public final void setPos(float x, float y) {
        throw new RuntimeException();
    }

    /**
     * Called when layout
     */
    public void onLayout(float left, float right, float y) {
        this.x1 = left;
        this.x2 = right;
        this.width = right - left;
        this.centerX = (left + right) / 2f;
        this.y1 = y;
        this.y2 = y + height;
    }

    protected abstract void onMouseHoverEnter();

    protected abstract void onMouseHoverExit();
}
