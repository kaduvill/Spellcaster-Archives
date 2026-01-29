package com.spellarchives.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.Slot;

import com.spellarchives.tile.TileSpellArchive;


/**
 * Server-side container for the Spellcaster's Archives GUI. No slots are exposed since the
 * backing storage is aggregated and accessed via capability calls and network messages.
 */
public class ContainerSpellArchive extends Container {
    private final TileSpellArchive tile;

    /**
     * Binds the container to a specific tile instance.
     *
     * @param playerInv The player's inventory (unused, kept for symmetry).
     * @param tile The Spellcaster's Archives tile entity.
     */
    public ContainerSpellArchive(InventoryPlayer playerInv, TileSpellArchive tile) {
        this.tile = tile;
        addPlayerInventorySlots(playerInv);
    }

    // Add the player's inventory slots so changes are propagated to the client while
    // the custom GUI is open. Uses offscreen coordinates to avoid rendering.
    private void addPlayerInventorySlots(InventoryPlayer playerInv) {
        final int OFFSCREEN_X = -9999;
        final int OFFSCREEN_Y = -9999;

        // Player main inventory (3 rows of 9)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int index = col + row * 9 + 9;
                this.addSlotToContainer(new Slot(playerInv, index, OFFSCREEN_X, OFFSCREEN_Y));
            }
        }

        // Hotbar (0-8)
        for (int col = 0; col < 9; col++) {
            this.addSlotToContainer(new Slot(playerInv, col, OFFSCREEN_X, OFFSCREEN_Y));
        }
    }

    /**
     * Allows interaction while the correct tile remains at the expected position and the
     * player is within 8 blocks (64 distance squared).
     *
     * @param playerIn The player attempting interaction.
     * @return True if the container can still be interacted with.
     */
    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return tile != null && tile.getWorld() != null && tile.getWorld().getTileEntity(tile.getPos()) == tile && playerIn.getDistanceSq(tile.getPos()) <= 64;
    }
}
