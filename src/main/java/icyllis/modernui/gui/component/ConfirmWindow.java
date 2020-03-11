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

package icyllis.modernui.gui.component;

import icyllis.modernui.api.animation.Animation;
import icyllis.modernui.api.animation.Applier;
import icyllis.modernui.api.element.Element;
import icyllis.modernui.gui.font.FontRendererTools;
import icyllis.modernui.gui.master.DrawTools;
import icyllis.modernui.gui.widget.TextButton;

public class ConfirmWindow extends Element {

    private String titleText;

    private String[] infoText;

    private float frameSizeHOffset;

    private float generalOpacity;

    private TextButton cancelButton;

    private TextButton.Countdown confirmButton;

    public ConfirmWindow(String title, String info, Runnable confirmOperation) {
        this(title, info, confirmOperation, 0);
    }

    /**
     * Constructor
     * @param confirmCountdown Countdown to confirm button available (unit-seconds)
     */
    public ConfirmWindow(String title, String info, Runnable confirmOperation, int confirmCountdown) {
        super(width -> width / 2f - 90f, height -> height / 2f - 40f);
        this.titleText = "Confirm " + title;
        infoText = FontRendererTools.splitStringToWidth(info, 164);
        cancelButton = new TextButton(w -> w / 2f + 80f, h -> h / 2f + 20f, "No", () -> moduleManager.closePopup());
        confirmButton = new TextButton.Countdown(w -> w / 2f + 74f - cancelButton.sizeW, h -> h / 2f + 20f, title, confirmOperation, confirmCountdown);
        cancelButton.setTextOpacity(0);
        confirmButton.setTextOpacity(0);
        moduleManager.addAnimation(new Animation(3, true)
                .applyTo(new Applier(0, 80, value -> frameSizeHOffset = value)));
        moduleManager.addAnimation(new Animation(3)
                .applyTo(new Applier(1, value -> {
                    generalOpacity = value;
                    cancelButton.setTextOpacity(value);
                    confirmButton.setTextOpacity(value);
                }))
                .withDelay(3));
    }

    @Override
    public void draw(float currentTime) {
        DrawTools.fillRectWithFrame(x, y, x + 180, y + frameSizeHOffset, 0.51f, 0x101010, 0.7f, 0x404040, 1.f);
        DrawTools.fillRectWithColor(x, y, x + 180, y + Math.min(frameSizeHOffset, 16), 0x080808, 0.7f);
        fontRenderer.drawString(titleText, x + 90, y + 4, 1, 1, 1, generalOpacity, 0.25f);
        int i = 0;
        for (String t : infoText) {
            fontRenderer.drawString(t, x + 8, y + 24 + i++ * 12, 1, 1, 1, generalOpacity, 0);
        }
        cancelButton.draw(currentTime);
        confirmButton.draw(currentTime);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        cancelButton.resize(width, height);
        confirmButton.resize(width, height);
    }

    @Override
    public void tick(int ticks) {
        confirmButton.tick(ticks);
    }
}