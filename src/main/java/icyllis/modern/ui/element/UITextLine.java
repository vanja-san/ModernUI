package icyllis.modern.ui.element;

import icyllis.modern.api.element.IElement;
import icyllis.modern.api.element.ITextLineTracker;
import icyllis.modern.ui.font.TrueTypeRenderer;

import java.util.function.Supplier;

public class UITextLine implements ITextLineTracker, IElement {

    private TrueTypeRenderer renderer = TrueTypeRenderer.DEFAULT_FONT_RENDERER;

    private float bx, by;
    private float x, y;
    private int color = -1;
    private boolean centered;
    private Supplier<String> text;

    @Override
    public void draw() {
        String bakedText = text.get();
        float rx = x;
        if(centered) {
            rx = x - renderer.getStringWidth(bakedText) / 2;
        }
        renderer.renderString(bakedText, rx, y, color);
    }

    @Override
    public ITextLineTracker text(Supplier<String> text) {
        this.text = text;
        return this;
    }

    @Override
    public ITextLineTracker pos(float x, float y) {
        bx = x;
        by = y;
        centered = false;
        return this;
    }

    @Override
    public ITextLineTracker pos(float x, float y, boolean center) {
        bx = x;
        by = y;
        centered = center;
        return this;
    }

    @Override
    public ITextLineTracker color(int color) {
        this.color = color;
        return this;
    }

    @Override
    public void resize(int width, int height) {
        x = width / 2f + bx;
        y = height / 2f + by;
    }
}