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

package icyllis.modernui.impl.background;

import icyllis.modernui.gui.animation.Animation;
import icyllis.modernui.gui.animation.Applier;
import icyllis.modernui.gui.master.Canvas;
import icyllis.modernui.gui.master.GlobalModuleManager;
import icyllis.modernui.gui.master.IDrawable;
import icyllis.modernui.impl.module.IngameMenuHome;

public class MenuSettingsBG implements IDrawable {

    private float w, h;

    private float xOffset;

    public MenuSettingsBG(IngameMenuHome home) {
        int c = home.getWindowWidth();
        if (home.getTransitionDirection(true)) {
            home.addAnimation(new Animation(4, true)
                    .applyTo(new Applier(-c, 0, v -> xOffset = v)));
        } else {
            home.addAnimation(new Animation(4, true)
                    .applyTo(new Applier(c, 0, v -> xOffset = v)));
        }
    }

    @Override
    public void draw(Canvas canvas, float time) {
        float x = 40, y = 16;
        canvas.translate(xOffset, 0);
        canvas.setRGBA(0, 0, 0, 0.377f);
        canvas.drawRect(x, y + 20, x + w, y + h - 20);
        canvas.setAlpha(0.755f);
        canvas.drawRect(x, y, x + w, y + 20);
        canvas.drawRect(x, y + h - 20, x + w, y + h);
        canvas.setRGBA(0.55f, 0.55f, 0.55f, 0.863f);
        canvas.drawLine(x, y + 20, x + w, y + 20);
        canvas.drawLine(x, y + h - 19.5f, x + w, y + h - 19.5f);
    }

    /*@Override
    public void draw(float time) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        float x = 40, y = 16;
        int alpha = 96;
        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        bufferBuilder.pos(x, y + sizeH - 20, 0.0D).color(0, 0, 0, alpha).endVertex();
        bufferBuilder.pos(x + sizeW, y + sizeH - 20, 0.0D).color(0, 0, 0, alpha).endVertex();
        bufferBuilder.pos(x + sizeW, y + 20, 0.0D).color(0, 0, 0, alpha).endVertex();
        bufferBuilder.pos(x, y + 20, 0.0D).color(0, 0, 0, alpha).endVertex();
        tessellator.draw();
        alpha = 192;
        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        bufferBuilder.pos(x, y + 20, 0.0D).color(0, 0, 0, alpha).endVertex();
        bufferBuilder.pos(x + sizeW, y + 20, 0.0D).color(0, 0, 0, alpha).endVertex();
        bufferBuilder.pos(x + sizeW, y, 0.0D).color(0, 0, 0, alpha).endVertex();
        bufferBuilder.pos(x, y, 0.0D).color(0, 0, 0, alpha).endVertex();
        tessellator.draw();
        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        bufferBuilder.pos(x, y + sizeH, 0.0D).color(0, 0, 0, alpha).endVertex();
        bufferBuilder.pos(x + sizeW, y + sizeH, 0.0D).color(0, 0, 0, alpha).endVertex();
        bufferBuilder.pos(x + sizeW, y + sizeH - 20, 0.0D).color(0, 0, 0, alpha).endVertex();
        bufferBuilder.pos(x, y + sizeH - 20, 0.0D).color(0, 0, 0, alpha).endVertex();
        tessellator.draw();
        bufferBuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        GL11.glLineWidth(1.0f);
        bufferBuilder.pos(x, y + 20, 0.0D).color(140, 140, 140, 220).endVertex();
        bufferBuilder.pos(x + sizeW, y + 20, 0.0D).color(140, 140, 140, 220).endVertex();
        bufferBuilder.pos(x, y + sizeH - 19.5, 0.0D).color(140, 140, 140, 220).endVertex();
        bufferBuilder.pos(x + sizeW, y + sizeH - 19.5, 0.0D).color(140, 140, 140, 220).endVertex();
        tessellator.draw();
        RenderSystem.enableTexture();
    }*/

    @Override
    public void resize(int width, int height) {
        w = width - 80;
        h = height - 32;
    }

    public void setXOffset(float xOffset) {
        this.xOffset = xOffset;
    }
}
