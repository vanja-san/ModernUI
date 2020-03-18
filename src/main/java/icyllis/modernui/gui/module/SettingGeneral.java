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

package icyllis.modernui.gui.module;

import com.google.common.collect.Lists;
import icyllis.modernui.gui.component.option.BooleanOptionEntry;
import icyllis.modernui.gui.component.option.OptionCategory;
import icyllis.modernui.gui.component.option.OptionEntry;
import icyllis.modernui.gui.component.option.SelectiveOptionEntry;
import icyllis.modernui.gui.element.IElement;
import icyllis.modernui.gui.master.GlobalModuleManager;
import icyllis.modernui.gui.master.IGuiModule;
import icyllis.modernui.gui.window.SettingScrollWindow;
import icyllis.modernui.system.SettingsManager;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.network.play.client.CLockDifficultyPacket;
import net.minecraft.network.play.client.CSetDifficultyPacket;
import net.minecraft.util.HandSide;
import net.minecraft.world.Difficulty;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SettingGeneral implements IGuiModule {

    private static Supplier<List<String>> DIFFICULTY_OPTIONS = () -> Lists.newArrayList(Difficulty.values()).stream().map(d -> d.getDisplayName().getFormattedText()).collect(Collectors.toCollection(ArrayList::new));

    private static Supplier<List<String>> MAIN_HANDS = () -> Lists.newArrayList(HandSide.values()).stream().map(HandSide::toString).collect(Collectors.toCollection(ArrayList::new));

    private Minecraft minecraft;

    private List<IElement> elements = new ArrayList<>();

    private List<IGuiEventListener> listeners = new ArrayList<>();

    private SettingScrollWindow window;

    private SelectiveOptionEntry difficultyEntry;

    private BooleanOptionEntry lockEntry;

    public SettingGeneral() {
        this.minecraft = Minecraft.getInstance();
        this.window = new SettingScrollWindow();
        addGameCategory();
        addSkinCategory();
        elements.add(window);
        listeners.add(window);
    }

    private void addGameCategory() {
        List<OptionEntry> list = new ArrayList<>();

        if (minecraft.world != null) {
            difficultyEntry = new SelectiveOptionEntry(window, I18n.format("options.difficulty"), DIFFICULTY_OPTIONS.get(),
                    minecraft.world.getDifficulty().getId(), i -> {
                Difficulty difficulty = Difficulty.values()[i];
                minecraft.getConnection().sendPacket(new CSetDifficultyPacket(difficulty));
            });
            list.add(difficultyEntry);
            if (minecraft.isSingleplayer() && !minecraft.world.getWorldInfo().isHardcore()) {
                boolean locked = minecraft.world.getWorldInfo().isDifficultyLocked();
                lockEntry = new BooleanOptionEntry(window, I18n.format("difficulty.lock.title"), locked, b -> {
                    if (b) {
                        GlobalModuleManager.INSTANCE.openPopup(new PopupLockDifficulty(this::lockDifficulty));
                    }
                }, true);
                difficultyEntry.setClickable(!locked);
                lockEntry.setClickable(!locked);
            } else {
                difficultyEntry.setClickable(false);
            }
            list.add(lockEntry);
        }

        list.add(SettingsManager.FOV.apply(window));
        list.add(SettingsManager.REALMS_NOTIFICATIONS.apply(window));

        OptionCategory gameCategory = new OptionCategory("Game", list);
        window.addEntry(gameCategory);
    }

    private void addSkinCategory() {
        List<OptionEntry> list = new ArrayList<>();
        GameSettings gameSettings = minecraft.gameSettings;

        for (PlayerModelPart part : PlayerModelPart.values()) {
            BooleanOptionEntry entry = new BooleanOptionEntry(window, part.getName().getFormattedText(),
                    gameSettings.getModelParts().contains(part), b -> gameSettings.setModelPartEnabled(part, b));
            list.add(entry);
        }
        OptionEntry mainHand = new SelectiveOptionEntry(window, I18n.format("options.mainHand"), MAIN_HANDS.get(), gameSettings.mainHand.ordinal(), i -> {
           gameSettings.mainHand = HandSide.values()[i];
           gameSettings.saveOptions();
           gameSettings.sendSettingsToServer();
        });
        list.add(mainHand);

        OptionCategory skinCategory = new OptionCategory("Skin", list);
        window.addEntry(skinCategory);
    }

    private void lockDifficulty() {
        if (this.minecraft.world != null) {
            this.minecraft.getConnection().sendPacket(new CLockDifficultyPacket(true));
            difficultyEntry.setClickable(false);
            lockEntry.setClickable(false);
        }
    }

    @Override
    public List<? extends IElement> getElements() {
        return elements;
    }

    @Override
    public List<? extends IGuiEventListener> getEventListeners() {
        return listeners;
    }
}