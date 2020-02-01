package icyllis.modern.ui.element;

import icyllis.modern.api.element.IVarTextBuilder;
import icyllis.modern.ui.font.IFontRenderer;
import icyllis.modern.ui.font.StringRenderer;

import java.util.function.Supplier;

public class UIVarText implements IVarTextBuilder, IElement {

    static final UIVarText DEFAULT = new UIVarText();
    private IFontRenderer renderer = StringRenderer.STRING_RENDERER;

    private float bx, by;
    private float x, y;
    private int color = 0xffffff;
    private int alpha = 0xff;
    private float align;
    private Supplier<String> text;

    public UIVarText() {
        text = () -> "";
    }

    @Override
    public void draw() {
        renderer.drawString(text.get(), x, y, color, alpha, align);
    }

    @Override
    public IVarTextBuilder text(Supplier<String> text) {
        this.text = text;
        return this;
    }

    @Override
    public IVarTextBuilder pos(float x, float y) {
        bx = x;
        by = y;
        return this;
    }

    @Override
    public IVarTextBuilder align(float align) {
        this.align = align;
        return this;
    }

    @Override
    public IVarTextBuilder color(int color) {
        this.color = color;
        return this;
    }

    @Override
    public void resize(int width, int height) {
        x = width / 2f + bx;
        y = height / 2f + by;
    }
}