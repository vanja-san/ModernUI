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

package icyllis.modernui.gui.widget;

import icyllis.modernui.font.FontTools;
import icyllis.modernui.font.TextAlign;
import icyllis.modernui.gui.master.*;
import icyllis.modernui.gui.math.Color3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Single line text input box
 */
//FIXME vanilla has many bugs, fuck
public class TextField extends Widget implements IKeyboardListener {

    private Predicate<String> filter = s -> true;

    private String text = "";

    private int maxStringLength = 32;

    private int lineScrollOffset;
    private int cursorPosition;
    private int selectionEnd;

    private boolean shiftDown = false;

    private float leftMargin = 2;
    private float rightMargin = 2;

    protected int timer = 0;
    protected boolean editing = false;

    @Nullable
    private Decoration decoration;

    @Nullable
    private Consumer<TextField> listener;

    private boolean runtimeUpdate;

    public TextField(Module module, float width, float height) {
        super(module, width, height);
    }

    public void setDecoration(@Nonnull Function<TextField, Decoration> function) {
        this.decoration = function.apply(this);
        width -= decoration.getHeaderLength();
    }

    public void setListener(@Nonnull Consumer<TextField> listener, boolean runtime) {
        this.listener = listener;
        runtimeUpdate = runtime;
    }

    public void setFilter(Predicate<String> filter) {
        this.filter = filter;
    }

    private void setMargin(float left, float right) {
        this.leftMargin = left;
        this.rightMargin = right;
    }

    public void setMaxStringLength(int length) {
        this.maxStringLength = length;
        if (this.text.length() > length) {
            this.text = this.text.substring(0, length);
            this.onTextChanged();
        }
    }

    @Override
    public void draw(@Nonnull Canvas canvas, float time) {
        if (decoration != null) {
            decoration.draw(canvas, time);
        }

        int ds = this.cursorPosition - this.lineScrollOffset;
        int de = this.selectionEnd - this.lineScrollOffset;
        String s = FontTools.trimStringToWidth(this.text.substring(this.lineScrollOffset), getVisibleWidth(), false);
        boolean b = ds >= 0 && ds <= s.length();

        float lx = x1 + leftMargin;
        float ty = y1 + (height - 8) / 2;
        float cx = lx;
        if (de > s.length()) {
            de = s.length();
        }

        canvas.setTextAlign(TextAlign.LEFT);

        if (!s.isEmpty()) {
            String s1 = b ? s.substring(0, ds) : s;
            canvas.setRGBA(0.88f, 0.88f, 0.88f, 1);
            canvas.drawText(s1, lx, ty);
            float c = FontTools.getStringWidth(s1);
            cx += c;
        }

        float kx = cx;
        if (!b) {
            kx = ds > 0 ? x2 - rightMargin : lx;
        }

        if (de != ds) {
            float l1 = lx + FontTools.getStringWidth(s.substring(0, de));
            canvas.setColor(Color3f.BLUE_C, 0.5f);
            canvas.drawRect(kx, ty - 1, l1, ty + 10);
        }

        canvas.setRGBA(0.88f, 0.88f, 0.88f, 1);

        if (!s.isEmpty() && b && ds < s.length()) {
            canvas.drawText(s.substring(ds), cx, ty);
        }

        if (editing && timer < 10) {
            canvas.drawRect(kx, ty - 1, kx + 0.5f, ty + 10);
        }
    }

    @Override
    public void tick(int ticks) {
        if (editing) {
            timer++;
            timer %= 20;
        }
    }

    /**
     * Sets the text of the text box, and moves the cursor to the end.
     */
    public void setText(String textIn) {
        if (filter.test(textIn)) {
            if (textIn.length() > this.maxStringLength) {
                this.text = textIn.substring(0, this.maxStringLength);
            } else {
                this.text = textIn;
            }

            this.setCursorToEnd();
            this.setSelectionPos(this.cursorPosition);
            this.onTextChanged();
        }
    }

    protected void onTextChanged() {
        if (listener != null && runtimeUpdate) {
            listener.accept(this);
        }
    }

    /**
     * Returns the contents of the text box
     */
    public String getText() {
        return text;
    }

    public int getMaxStringLength() {
        return maxStringLength;
    }

    public float getLeftMargin() {
        return leftMargin;
    }

    public float getRightMargin() {
        return rightMargin;
    }

    @Nullable
    public Decoration getDecoration() {
        return decoration;
    }

    public void writeText(String textToWrite) {
        String result = "";
        String filter = SharedConstants.filterAllowedCharacters(textToWrite);
        int i = Math.min(this.cursorPosition, this.selectionEnd);
        int j = Math.max(this.cursorPosition, this.selectionEnd);
        int canWriteCount = this.maxStringLength - this.text.length() - (i - j);

        if (!this.text.isEmpty()) {
            result = result + this.text.substring(0, i); // write text that before cursor and without selected
        }

        int l;
        if (canWriteCount < filter.length()) {
            result = result + filter.substring(0, canWriteCount); // ignore excess part
            l = canWriteCount;
        } else {
            result = result + filter;
            l = filter.length();
        }

        if (!this.text.isEmpty() && j < this.text.length()) { // write text that after cursor
            result = result + this.text.substring(j);
        }

        if (this.filter.test(result)) { // if result is legal
            this.text = result;
            setCursorPos(i + l);
            setSelectionPos(cursorPosition);
            this.onTextChanged();
        }
    }

    public void setSelectionPos(int position) {
        int i = this.text.length();
        this.selectionEnd = MathHelper.clamp(position, 0, i);

        if (this.lineScrollOffset > i) {
            this.lineScrollOffset = i;
        }

        String s = FontTools.trimStringToWidth(this.text.substring(this.lineScrollOffset), getVisibleWidth(), false);
        int k = s.length() + this.lineScrollOffset;
        if (this.selectionEnd == this.lineScrollOffset) {
            this.lineScrollOffset -= FontTools.trimStringToWidth(this.text, getVisibleWidth(), true).length();
        }

        if (this.selectionEnd > k) {
            this.lineScrollOffset += this.selectionEnd - k;
        } else if (this.selectionEnd <= this.lineScrollOffset) {
            this.lineScrollOffset -= this.lineScrollOffset - this.selectionEnd;
        }

        this.lineScrollOffset = MathHelper.clamp(this.lineScrollOffset, 0, i);
        timer = 0;
    }

    public void setCursorToEnd() {
        this.setCursorPos(text.length());
    }

    public void setCursorPos(int pos) {
        this.cursorPosition = MathHelper.clamp(pos, 0, this.text.length());
        if (!shiftDown) {
            this.setSelectionPos(cursorPosition);
        }
    }

    @Override
    public void setPos(float x, float y) {
        if (decoration != null) {
            float c = decoration.getHeaderLength();
            x += c;
        }
        super.setPos(x, y);
    }

    @Override
    protected void onMouseHoverEnter() {
        super.onMouseHoverEnter();
        MouseTools.useIBeamCursor();
    }

    @Override
    protected void onMouseHoverExit() {
        super.onMouseHoverExit();
        MouseTools.useDefaultCursor();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (listening && mouseButton == 0) {
            if (!editing) {
                startEditing();
            }
            if (mouseX >= x1 + leftMargin && mouseX <= x2 - rightMargin) {
                float i = (float) (mouseX - x1 - leftMargin);
                String s = FontTools.trimStringToWidth(this.text.substring(this.lineScrollOffset), getVisibleWidth(), false);
                shiftDown = Screen.hasShiftDown();
                // FIX vanilla's bug
                this.setCursorPos(FontTools.trimStringToWidth(s, i, false).length() + this.lineScrollOffset);
            }
            return true;
        }
        return false;
    }

    private void startEditing() {
        module.setKeyboardListener(this);
        editing = true;
        timer = 0;
    }

    @Override
    public void stopKeyboardListening() {
        editing = false;
        timer = 0;
        if (listener != null) {
            listener.accept(this);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        this.shiftDown = Screen.hasShiftDown();
        if (Screen.isSelectAll(keyCode)) {
            this.setCursorToEnd();
            this.setSelectionPos(0);
            return true;
        } else if (Screen.isCopy(keyCode)) {
            Minecraft.getInstance().keyboardListener.setClipboardString(getSelectedText());
            return true;
        } else if (Screen.isPaste(keyCode)) {
            this.writeText(Minecraft.getInstance().keyboardListener.getClipboardString());
            return true;
        } else if (Screen.isCut(keyCode)) {
            Minecraft.getInstance().keyboardListener.setClipboardString(getSelectedText());
            this.writeText("");
            return true;
        } else {
            switch (keyCode) {
                case GLFW.GLFW_KEY_ESCAPE:
                    module.setKeyboardListener(null);
                    return true;
                case GLFW.GLFW_KEY_BACKSPACE:
                    this.shiftDown = false;
                    this.delete(-1);
                    this.shiftDown = Screen.hasShiftDown();
                    return true;
                case GLFW.GLFW_KEY_DELETE:
                    this.shiftDown = false;
                    this.delete(1);
                    this.shiftDown = Screen.hasShiftDown();
                    return true;
                case GLFW.GLFW_KEY_RIGHT:
                    if (Screen.hasControlDown()) {
                        this.setCursorPos(this.getNthWordFromCursor(1));
                    } else {
                        this.moveCursorBy(1);
                    }
                    return true;
                case GLFW.GLFW_KEY_LEFT:
                    if (Screen.hasControlDown()) {
                        this.setCursorPos(this.getNthWordFromCursor(-1));
                    } else {
                        this.moveCursorBy(-1);
                    }
                    return true;
                case GLFW.GLFW_KEY_HOME:
                    this.setCursorPos(0);
                    return true;
                case GLFW.GLFW_KEY_END:
                    this.setCursorToEnd();
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (SharedConstants.isAllowedCharacter(codePoint)) {
            writeText(Character.toString(codePoint));
            return true;
        }
        return false;
    }

    private void delete(int num) {
        if (Screen.hasControlDown()) {
            deleteWords(num);
        } else {
            deleteFromCursor(num);
        }
    }

    public void deleteWords(int num) {
        if (!this.text.isEmpty()) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                this.deleteFromCursor(this.getNthWordFromCursor(num) - this.cursorPosition);
            }
        }
    }

    public void deleteFromCursor(int num) {
        if (!this.text.isEmpty()) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                boolean reverse = num < 0;

                int left = reverse ? this.cursorPosition + num : this.cursorPosition;
                int right = reverse ? this.cursorPosition : this.cursorPosition + num;

                String result = "";

                if (left >= 0) {
                    result = this.text.substring(0, left);
                }

                if (right < this.text.length()) {
                    result = result + this.text.substring(right);
                }

                if (this.filter.test(result)) {
                    this.text = result;
                    if (reverse) {
                        this.moveCursorBy(num);
                    }
                    this.onTextChanged();
                }
            }
        }
    }

    public void moveCursorBy(int offset) {
        this.setCursorPos(this.cursorPosition + offset);
    }

    /**
     * Get visible width if there's margin
     */
    public float getVisibleWidth() {
        return width - leftMargin - rightMargin;
    }

    @Nonnull
    private String getSelectedText() {
        int left = Math.min(this.cursorPosition, this.selectionEnd);
        int right = Math.max(this.cursorPosition, this.selectionEnd);
        return text.substring(left, right);
    }

    /**
     * Gets the starting index of the word at the specified number of words away from the cursor position.
     */
    public int getNthWordFromCursor(int numWords) {
        return this.getNthWordFromPos(numWords, cursorPosition);
    }

    /**
     * Gets the starting index of the word at a distance of the specified number of words away from the given position.
     */
    private int getNthWordFromPos(int num, int pos) {
        return this.getNthWordFromPosWS(num, pos);
    }

    /**
     * Like getNthWordFromPos (which wraps this), but adds option for skipping consecutive spaces
     */
    private int getNthWordFromPosWS(int num, int pos) {
        int i = pos;
        boolean reverse = num < 0;
        int amount = Math.abs(num);

        for (int k = 0; k < amount; ++k) {
            if (!reverse) {
                int l = this.text.length();
                i = this.text.indexOf(32, i);
                if (i == -1) {
                    i = l;
                } else {
                    while (i < l && this.text.charAt(i) == ' ') {
                        ++i;
                    }
                }
            } else {
                while (i > 0 && this.text.charAt(i - 1) == ' ') {
                    --i;
                }

                while (i > 0 && this.text.charAt(i - 1) != ' ') {
                    --i;
                }
            }
        }

        return i;
    }

    public static abstract class Decoration {

        protected final TextField instance;

        public Decoration(TextField instance) {
            this.instance = instance;
        }

        public abstract void draw(@Nonnull Canvas canvas, float time);

        public abstract float getHeaderLength();
    }

    public static class Frame extends Decoration {

        @Nullable
        private final String title;

        private final float titleLength;

        private float r, g, b, a;

        public Frame(TextField instance, @Nullable String title, int color) {
            super(instance);
            this.title = title;
            this.titleLength = FontTools.getStringWidth(title);
            setColor(color);
        }

        @Override
        public void draw(@Nonnull Canvas canvas, float time) {
            canvas.setRGBA(0, 0, 0, 0.5f);
            canvas.drawRect(instance.x1 - getHeaderLength(), instance.y1, instance.x2, instance.y2);
            canvas.setRGBA(r, g, b, a);
            canvas.drawRectOutline(instance.x1 - getHeaderLength(), instance.y1, instance.x2, instance.y2, 0.51f);
            if (title != null) {
                canvas.setTextAlign(TextAlign.LEFT);
                canvas.drawText(title, instance.x1 - titleLength, instance.y1 + (instance.height - 8) / 2f);
            }
        }

        @Override
        public float getHeaderLength() {
            if (titleLength == 0) {
                return 0;
            }
            return titleLength + 2;
        }

        public void setColor(int color) {
            a = (color >> 24 & 0xff) / 255f;
            r = (color >> 16 & 0xff) / 255f;
            g = (color >> 8 & 0xff) / 255f;
            b = (color & 0xff) / 255f;
        }

    }
}
