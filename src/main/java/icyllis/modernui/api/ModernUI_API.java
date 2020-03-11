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

package icyllis.modernui.api;

import icyllis.modernui.api.manager.IModuleManager;
import icyllis.modernui.api.manager.INetworkManager;
import icyllis.modernui.api.manager.IGuiManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum ModernUI_API {
    INSTANCE;

    private INetworkManager network;

    private IGuiManager gui;

    private IModuleManager module;

    public INetworkManager getNetworkManager() {
        return network;
    }

    @OnlyIn(Dist.CLIENT)
    public IGuiManager getGuiManager() {
        return gui;
    }

    @OnlyIn(Dist.CLIENT)
    public IModuleManager getModuleManager() {
        return module;
    }

}