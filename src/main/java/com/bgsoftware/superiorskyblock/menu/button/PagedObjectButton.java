package com.bgsoftware.superiorskyblock.menu.button;

import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.utils.items.ItemBuilder;
import com.bgsoftware.superiorskyblock.wrappers.SoundWrapper;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class PagedObjectButton<T> extends SuperiorMenuButton {

    private final ItemBuilder nullItem;

    protected T pagedObject = null;
    protected SuperiorPlayer targetPlayer;

    protected PagedObjectButton(ItemBuilder buttonItem, SoundWrapper clickSound, List<String> commands,
                                String requiredPermission, SoundWrapper lackPermissionSound,
                                ItemBuilder nullItem) {
        super(buttonItem, clickSound, commands, requiredPermission, lackPermissionSound);
        this.nullItem = nullItem == null ? new ItemBuilder(Material.AIR) : nullItem;
    }

    public void updateTarget(T pagedObject, SuperiorPlayer targetPlayer) {
        this.pagedObject = pagedObject;
        this.targetPlayer = targetPlayer;
    }

    public ItemBuilder getNullItem() {
        return nullItem.clone();
    }

    @Nullable
    @Override
    public ItemBuilder getButtonItem() {
        return modifyButtonItem(super.getButtonItem(), pagedObject);
    }

    public abstract ItemBuilder modifyButtonItem(ItemBuilder buttonItem, T pagedObject);

    @SuppressWarnings("unchecked")
    public static abstract class PagedObjectBuilder<B extends AbstractBuilder<B, T>, T extends SuperiorMenuButton>
            extends AbstractBuilder<B, T> {

        protected ItemBuilder nullItem = null;

        public B setNullItem(ItemBuilder nullItem) {
            this.nullItem = nullItem;
            return (B) this;
        }

    }

}
