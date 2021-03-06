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

package icyllis.modernui.impl.stats;

import icyllis.modernui.font.TextAlign;
import icyllis.modernui.font.TrueTypeRenderer;
import icyllis.modernui.gui.master.Canvas;
import icyllis.modernui.gui.scroll.ScrollWindow;
import icyllis.modernui.gui.scroll.UniformScrollEntry;
import icyllis.modernui.gui.math.Color3f;
import net.minecraft.client.resources.I18n;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class GeneralStatsEntry extends UniformScrollEntry {

    private final Stat<ResourceLocation> stat;

    private final Color3f color;

    private final String title;

    private String value = "";

    public GeneralStatsEntry(ScrollWindow<?> window, @Nonnull Stat<ResourceLocation> stat, Color3f color) {
        super(window, GeneralStatsGroup.ENTRY_HEIGHT);
        this.stat = stat;
        this.color = color;
        title = I18n.format("stat." + stat.getValue().toString().replace(':', '.'));
    }

    @Override
    public void draw(Canvas canvas, float time) {
        canvas.setColor(color, 1);
        canvas.setTextAlign(TextAlign.LEFT);
        canvas.drawText(title, x1 + 2, y1 + 1);
        canvas.setTextAlign(TextAlign.RIGHT);
        canvas.drawText(value, x2 - 2, y1 + 1);
    }

    public void updateValue(@Nonnull StatisticsManager manager) {
        value = stat.format(manager.getValue(stat));
    }

    @Override
    protected void onMouseHoverEnter() {

    }

    @Override
    protected void onMouseHoverExit() {

    }
}
