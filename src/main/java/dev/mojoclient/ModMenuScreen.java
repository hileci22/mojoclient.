package dev.mojoclient;

import dev.mojoclient.modules.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ModMenuScreen extends Screen {
    private static final int PER_PAGE = 6, ROWS = 3;
    private static final int PANEL = 0xFF1B1C20, HEADER = 0xFF23252B, CARD = 0xFF2A2C33, BLUE = 0xFF3B82F6;

    private final Screen parent;
    private String query = "";
    private int page = 0;
    private int px, py, pw, ph, cardW;
    private final List<Module> shown = new ArrayList<>();

    public ModMenuScreen(Screen parent) {
        super(Text.literal("MojoClient"));
        this.parent = parent;
    }

    private Text label(Module m) {
        return Text.literal(m.name);
    }

    @Override
    protected void init() {
        pw = Math.min(width - 10, 420);
        ph = Math.min(height - 10, 200);
        px = (width - pw) / 2;
        py = (height - ph) / 2;
        cardW = (pw - 24) / 2;

        shown.clear();
        for (Module m : MojoClient.MODULES) {
            if (query.isEmpty() || m.name.toLowerCase().contains(query.toLowerCase())) shown.add(m);
        }
        int pages = Math.max(1, (shown.size() + PER_PAGE - 1) / PER_PAGE);
        page = Math.min(page, pages - 1);

        TextFieldWidget search = new TextFieldWidget(textRenderer, px + pw - 138, py + 5, 130, 18, Text.literal("Ara"));
        search.setPlaceholder(Text.literal("Ara..."));
        search.setText(query);
        search.setChangedListener(s -> { query = s; page = 0; });
        addDrawableChild(search);

        int start = page * PER_PAGE;
        for (int i = 0; i < PER_PAGE && start + i < shown.size(); i++) {
            Module m = shown.get(start + i);
            int col = i % 2, row = i / 2;
            int x = px + 8 + col * (cardW + 8);
            int y = py + 34 + row * 44;
            addDrawableChild(ButtonWidget.builder(label(m), b -> m.toggle())
                    .dimensions(x, y, cardW, 22).build());
        }

        int by = py + ph - 24;
        addDrawableChild(ButtonWidget.builder(Text.literal("<"), b -> { if (page > 0) { page--; clearAndInit(); } })
                .dimensions(px + 8, by, 30, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal(">"), b -> { if (page < pages - 1) { page++; clearAndInit(); } })
                .dimensions(px + 42, by, 30, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Ara/Yenile"), b -> clearAndInit())
                .dimensions(px + pw / 2 - 40, by, 80, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Kapat"), b -> close())
                .dimensions(px + pw - 78, by, 70, 20).build());
    }

    @Override
    public void renderBackground(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.renderBackground(ctx, mouseX, mouseY, delta);
        ctx.fill(px, py, px + pw, py + ph, PANEL);
        ctx.fill(px, py, px + pw, py + 28, HEADER);
        int start = page * PER_PAGE;
        for (int i = 0; i < PER_PAGE && start + i < shown.size(); i++) {
            int col = i % 2, row = i / 2;
            int x = px + 8 + col * (cardW + 8);
            int y = py + 34 + row * 44;
            ctx.fill(x, y, x + cardW, y + 40, CARD);
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        ctx.drawTextWithShadow(textRenderer, Text.literal("MojoClient"), px + 10, py + 10, 0xFFFFFFFF);
        int start = page * PER_PAGE;
        for (int i = 0; i < PER_PAGE && start + i < shown.size(); i++) {
            Module m = shown.get(start + i);
            int col = i % 2, row = i / 2;
            int x = px + 8 + col * (cardW + 8);
            int y = py + 34 + row * 44;
            if (m.enabled) ctx.fill(x + cardW - 4, y, x + cardW, y + 40, BLUE);
            String d = m.description;
            int maxW = cardW - 12;
            while (textRenderer.getWidth(d) > maxW && d.length() > 3) d = d.substring(0, d.length() - 4) + "...";
            ctx.drawTextWithShadow(textRenderer, Text.literal(d), x + 4, y + 27, m.enabled ? BLUE : 0xFF9AA0AA);
        }
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }
}
