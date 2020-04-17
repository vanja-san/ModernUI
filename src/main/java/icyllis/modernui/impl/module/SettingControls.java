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

package icyllis.modernui.impl.module;

import com.google.common.collect.Lists;
import icyllis.modernui.gui.master.GlobalModuleManager;
import icyllis.modernui.gui.master.Module;
import icyllis.modernui.gui.widget.*;
import icyllis.modernui.impl.setting.SettingCategoryGroup;
import icyllis.modernui.impl.setting.SettingEntry;
import icyllis.modernui.impl.setting.KeyBindingEntry;
import icyllis.modernui.gui.scroll.SettingScrollWindow;
import icyllis.modernui.system.ModernUI;
import icyllis.modernui.system.SettingsManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ControlsScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SettingControls extends Module {

    private Minecraft minecraft;

    private SettingScrollWindow window;
    private SettingCategoryGroup landmarkGroup;

    private List<KeyBindingEntry> allKeyBinding = new ArrayList<>();

    private TextField searchBox;
    private TriangleButton nextButton;
    private TriangleButton previousButton;

    private KeyBindingEntry currentResult;
    private List<KeyBindingEntry> searchResults = new ArrayList<>();

    private StaticFrameButton showConflictButton;
    private StaticFrameButton resetAllButton;

    private DropDownWidget searchModeButton;

    public SettingControls() {
        this.minecraft = Minecraft.getInstance();
        this.window = new SettingScrollWindow(this);

        List<SettingCategoryGroup> groups = new ArrayList<>();

        addMouseCategory(groups);
        addAllKeyBindings(groups);

        window.addGroups(groups);

        addWidget(window);

        showConflictButton = new StaticFrameButton(this, 68, "Show Conflicts", this::filterConflicts, true);
        addWidget(showConflictButton);

        resetAllButton = new StaticFrameButton(this, 68, I18n.format("controls.resetAll"), this::resetAllKey, true);
        addWidget(resetAllButton);

        searchModeButton = new DropDownWidget(this, Lists.newArrayList("Name", "Key"), 0, i -> {}, DropDownMenu.Align.RIGHT);
        addWidget(searchModeButton);

        searchBox = new TextField(this, 100, 12);
        searchBox.setDecoration(t -> new TextField.Frame(t, null, 0xffc0c0c0));
        searchBox.setListener(this::searchBoxCallback, true);
        addWidget(searchBox);

        nextButton = new TriangleButton(this, TriangleButton.Direction.DOWN, 12, this::locateNextResult, false);
        previousButton = new TriangleButton(this, TriangleButton.Direction.UP, 12, this::locatePreviousResult, false);
        addWidget(nextButton);
        addWidget(previousButton);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        searchModeButton.setPos(width / 2f - 122, height - 34);
        resetAllButton.setPos(width / 2f + 88, height - 32);
        showConflictButton.setPos(width / 2f + 16, height - 32);
        searchBox.setPos(width / 2f - 120, height - 32);
        nextButton.setPos(width / 2f, height - 32);
        previousButton.setPos(width / 2f - 16, height - 32);
    }

    private void filterConflicts() {
        searchResults.clear();
        searchBox.setText("");
        allKeyBinding.stream().filter(f -> f.getTier() > 0).forEach(searchResults::add);
        if (!searchResults.isEmpty()) {
            nextButton.setClickable(true);
            previousButton.setClickable(true);
            currentResult = searchResults.get(0);
            currentResult.lightUp();
            landmarkGroup.followEntry(currentResult);
        } else {
            nextButton.setClickable(false);
            previousButton.setClickable(false);
        }
    }

    private void searchBoxCallback(@Nonnull TextField textField) {
        searchResults.clear();
        String t = textField.getText();
        if (!t.isEmpty()) {
            String ct = t.toLowerCase();
            if (searchModeButton.getIndex() == 0) {
                allKeyBinding.stream().filter(f -> f.title.toLowerCase().contains(ct)).forEach(searchResults::add);
            } else {
                allKeyBinding.stream().filter(f ->
                        Objects.equals(TextFormatting.getTextWithoutFormattingCodes(
                                f.getInputBox().getKeyText().toLowerCase()), ct)).forEach(searchResults::add);
            }
            if (!searchResults.isEmpty()) {
                if (searchResults.size() > 1) {
                    nextButton.setClickable(true);
                    previousButton.setClickable(true);
                } else {
                    nextButton.setClickable(false);
                    previousButton.setClickable(false);
                }
                ((TextField.Frame) Objects.requireNonNull(searchBox.getDecoration())).setColor(0xffc0c0c0);
                currentResult = searchResults.get(0);
                currentResult.lightUp();
                landmarkGroup.followEntry(currentResult);
            } else {
                ((TextField.Frame) Objects.requireNonNull(searchBox.getDecoration())).setColor(0xffff5555);
                nextButton.setClickable(false);
                previousButton.setClickable(false);
            }
        } else {
            ((TextField.Frame) Objects.requireNonNull(searchBox.getDecoration())).setColor(0xffc0c0c0);
            nextButton.setClickable(false);
            previousButton.setClickable(false);
        }
    }

    private void resetAllKey() {
        for(KeyBinding keybinding : minecraft.gameSettings.keyBindings) {
            keybinding.setToDefault();
        }
        KeyBinding.resetKeyBindingArrayAndHash();
        allKeyBinding.forEach(KeyBindingEntry::updateKeyText);
        checkAllConflicts();
    }

    private void locateNextResult() {
        int i = searchResults.indexOf(currentResult) + 1;
        if (i >= searchResults.size()) {
            currentResult = searchResults.get(0);
        } else {
            currentResult = searchResults.get(i);
        }
        currentResult.lightUp();
        landmarkGroup.followEntry(currentResult);
    }

    private void locatePreviousResult() {
        int i = searchResults.indexOf(currentResult) - 1;
        if (i < 0) {
            currentResult = searchResults.get(searchResults.size() - 1);
        } else {
            currentResult = searchResults.get(i);
        }
        currentResult.lightUp();
        landmarkGroup.followEntry(currentResult);
    }

    private void addMouseCategory(List<SettingCategoryGroup> groups) {
        List<SettingEntry> list = new ArrayList<>();

        list.add(SettingsManager.SENSITIVITY.apply(window));
        list.add(SettingsManager.MOUSE_WHEEL_SENSITIVITY.apply(window));
        list.add(SettingsManager.INVERT_MOUSE.apply(window));
        list.add(SettingsManager.DISCRETE_MOUSE_WHEEL.apply(window));
        list.add(SettingsManager.TOUCHSCREEN.apply(window));
        if (InputMappings.func_224790_a()) {
            list.add(SettingsManager.RAW_MOUSE_INPUT.apply(window));
        }

        SettingCategoryGroup categoryGroup = new SettingCategoryGroup(window, I18n.format("gui.modernui.settings.category.mouse"), list);
        groups.add(categoryGroup);
    }

    private void addAllKeyBindings(List<SettingCategoryGroup> groups) {
        KeyBinding[] keyBindings = ArrayUtils.clone(minecraft.gameSettings.keyBindings);

        //Sort by category and key desc {@link KeyBinding#compareTo(KeyBinding)}
        Arrays.sort(keyBindings);

        String categoryKey = null;
        List<SettingEntry> list = null;

        for (KeyBinding keybinding : keyBindings) {
            String ck = keybinding.getKeyCategory();
            if (!ck.equals(categoryKey)) {
                if (list != null) {
                    SettingCategoryGroup category = new SettingCategoryGroup(window, I18n.format(categoryKey), list);
                    groups.add(category);
                }
                categoryKey = ck;
                list = new ArrayList<>();
            }
            KeyBindingEntry entry = new KeyBindingEntry(window, keybinding, this::checkAllConflicts);
            list.add(entry);
            allKeyBinding.add(entry);
            if (allKeyBinding.size() >= 1000) {
                ModernUI.LOGGER.warn(GlobalModuleManager.MARKER, "Too much key bindings, please report this issue");
                // maybe we want more optimization?
                break;
            }
        }
        // add last category
        if (categoryKey != null) {
            landmarkGroup = new SettingCategoryGroup(window, I18n.format(categoryKey), list);
            groups.add(landmarkGroup);
        }

        checkAllConflicts();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (super.mouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }
        if (getKeyboardListener() != null) {
            setKeyboardListener(null);
            return true;
        }
        return false;
    }

    /**
     * If a key conflicts with another key, they both are conflicted
     * Called when a key binding changed, but vanilla does this every frame
     * so it's much better than vanilla, I don't have to do more optimization
     */
    private void checkAllConflicts() {
        KeyBinding[] keyBindings = minecraft.gameSettings.keyBindings;
        KeyBinding keyBinding;
        for (KeyBindingEntry entry : allKeyBinding) {
            int conflict = 0;
            keyBinding = entry.getKeyBinding();
            if (!keyBinding.isInvalid()) {
                for (KeyBinding other : keyBindings) {
                    if (keyBinding != other) { // there's a same key binding
                        conflict = Math.max(conflict, conflicts(keyBinding, other));
                    }
                }
            }
            entry.setConflictTier(conflict);
        }
    }

    /**
     * different from forge, finally return false, and in-game conflict should be mutual
     * this is quicker than forge's, because we reduced checks, return 1 instead
     */
    private int conflicts(KeyBinding a, KeyBinding t) {
        IKeyConflictContext conflictContext = a.getKeyConflictContext();
        IKeyConflictContext otherConflictContext = t.getKeyConflictContext();
        if (conflictContext.conflicts(otherConflictContext) || otherConflictContext.conflicts(conflictContext)) {
            KeyModifier keyModifier = a.getKeyModifier();
            KeyModifier otherKeyModifier = t.getKeyModifier();
            if (keyModifier.matches(t.getKey()) || otherKeyModifier.matches(a.getKey())) {
                return 1;
            } else if (a.getKey().equals(t.getKey())) {
                if (keyModifier == otherKeyModifier ||
                        ((conflictContext.conflicts(KeyConflictContext.IN_GAME) ||
                                otherConflictContext.conflicts(KeyConflictContext.IN_GAME)) &&
                                (keyModifier == KeyModifier.NONE || otherKeyModifier == KeyModifier.NONE)))
                return 2;
            }
        }
        return 0;
    }

}
