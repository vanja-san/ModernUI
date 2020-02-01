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

package icyllis.modern.api.element;

import java.util.function.Supplier;

public interface IConstTextBuilder {

    IConstTextBuilder text(String c);

    IConstTextBuilder pos(float x, float y);

    /**
     * Text alignment: 0 = left, 0.25 = center, 0.5 = right
     * @param align align
     */
    IConstTextBuilder align(float align);

    IConstTextBuilder color(Supplier<Integer> color);

    IConstTextBuilder scale(Supplier<Float> scale);

    IConstTextBuilder style();

    IConstTextAnimator animated();
}