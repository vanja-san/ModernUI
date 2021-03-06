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

import icyllis.modernui.gui.scroll.ScrollWindow;
import icyllis.modernui.gui.scroll.UniformScrollGroup;
import icyllis.modernui.gui.math.Color3f;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.stats.Stats;
import net.minecraft.util.ResourceLocation;

/**
 * Text-to-value statistics tab
 */
public class GeneralStatsGroup extends UniformScrollGroup<GeneralStatsEntry> {

    public static int ENTRY_HEIGHT = 12;

    public GeneralStatsGroup(ScrollWindow<?> window) {
        super(window, ENTRY_HEIGHT);
        int i = 0;
        for (Stat<ResourceLocation> stat : Stats.CUSTOM) {
            entries.add(new GeneralStatsEntry(window, stat, (i & 1) == 0 ? Color3f.WHILE : Color3f.GRAY));
            i++;
        }
        height = entries.size() * entryHeight;
    }

    @Override
    public void onLayout(float left, float right, float y) {
        super.onLayout(left, right, y);
        left = centerX - 120;
        right = centerX + 120;
        int i = 0;
        for (GeneralStatsEntry entry : entries) {
            float cy = y + i * entryHeight;
            entry.onLayout(left, right, cy);
            i++;
        }
    }

    public void updateValues(StatisticsManager manager) {
        entries.forEach(e -> e.updateValue(manager));
    }
}
