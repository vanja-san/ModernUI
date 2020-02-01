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

package icyllis.modern.ui.blur;

import icyllis.modern.system.ModernUI;
import icyllis.modern.ui.master.GlobalAnimationManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.ClientResourcePackInfo;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderDefault;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.PackCompatibility;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public enum BlurHandler {
    INSTANCE;

    private DummyResourcePack sp = new DummyResourcePack();

    private Field shaders;

    private boolean initializing;

    BlurHandler() {
        shaders = ObfuscationReflectionHelper.findField(ShaderGroup.class, "field_148031_d");
        DistExecutor.runWhenOn(Dist.CLIENT, () -> this::addShaderPack);
    }

    public void addShaderPack() {
        ResourcePackList<ClientResourcePackInfo> rps = ObfuscationReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getInstance(), "field_110448_aq");
        rps.addPackFinder(new IPackFinder() {
            @SuppressWarnings({"unchecked", "deprecation"})
            @Override
            public <T extends ResourcePackInfo> void addPackInfosToMap(Map<String, T> nameToPackMap, ResourcePackInfo.IFactory<T> packInfoFactory) {
                T pack = (T) new ClientResourcePackInfo(ModernUI.MODID + "_blur", true, () -> sp, new StringTextComponent(sp.getName()), new StringTextComponent(""),
                        PackCompatibility.COMPATIBLE, ResourcePackInfo.Priority.BOTTOM, true, null);
                nameToPackMap.put(ModernUI.MODID + "_blur", pack);
            }
        });
    }

    public void blur(boolean hasGui) {
        if (Minecraft.getInstance().world != null) {
            GameRenderer gr = Minecraft.getInstance().gameRenderer;
            if(gr.getShaderGroup() == null && hasGui) {
                gr.loadShader(new ResourceLocation("shaders/post/fade_in_blur.json"));
                initializing = true;
            } else if(gr.getShaderGroup() != null && !hasGui) {
                gr.stopUseShader();
                initializing = false;
            }
        }
    }

    public void tick() {
        if(initializing) {
            float p = Math.min(GlobalAnimationManager.INSTANCE.time(), 4.0f);
            this.updateUniform("Progress", p);
            if(p == 4.0f) {
                initializing = false;
            }
        }
    }

    private void updateUniform(String name, float value) {
        ShaderGroup sg = Minecraft.getInstance().gameRenderer.getShaderGroup();
        if(sg == null)
            return;
        try {
            @SuppressWarnings("unchecked")
            List<Shader> shaders = (List<Shader>) this.shaders.get(sg);
            for (Shader s : shaders) {
                ShaderDefault u = s.getShaderManager().getShaderUniform(name);
                u.set(value);
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}