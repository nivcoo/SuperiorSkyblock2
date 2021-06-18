package com.bgsoftware.superiorskyblock.registry;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.SortingType;
import com.bgsoftware.superiorskyblock.island.IslandPosition;
import com.bgsoftware.superiorskyblock.utils.registry.Registry;
import com.bgsoftware.superiorskyblock.utils.registry.SortedRegistry;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class IslandRegistry extends SortedRegistry<UUID, Island, SortingType> {

    private static final Predicate<Island> ISLANDS_PREDICATE = island -> !island.isIgnored();

    private final Registry<IslandPosition, Island> islandsByPositions = createRegistry();
    private final Registry<UUID, Island> islandsByUUID = createRegistry();
    private final SuperiorSkyblockPlugin plugin;

    public IslandRegistry(SuperiorSkyblockPlugin plugin){
        this.plugin = plugin;
        SortingType.values().forEach(sortingType -> registerSortingType(sortingType, false, ISLANDS_PREDICATE));
    }

    public Island get(Location location){
        Island island = islandsByPositions.get(IslandPosition.of(location));
        return island == null || !island.isInside(location) ? null : island;
    }

    public Island getByUUID(UUID uuid){
        return islandsByUUID.get(uuid);
    }

    public Island add(UUID uuid, Island island){
        Location islandLocation = island.getCenter(plugin.getSettings().defaultWorldEnvironment);
        islandsByPositions.add(IslandPosition.of(islandLocation), island);

        if(plugin.getProviders().hasCustomWorldsSupport()){
            runWithCustomWorld(islandLocation, island, World.Environment.NORMAL,
                    location -> islandsByPositions.add(IslandPosition.of(location), island));
            runWithCustomWorld(islandLocation, island, World.Environment.NETHER,
                    location -> islandsByPositions.add(IslandPosition.of(location), island));
            runWithCustomWorld(islandLocation, island, World.Environment.THE_END,
                    location -> islandsByPositions.add(IslandPosition.of(location), island));
        }

        islandsByUUID.add(island.getUniqueId(), island);

        return super.add(uuid, island);
    }

    @Override
    public Island remove(UUID uuid){
        Island island = super.remove(uuid);
        if(island != null) {
            Location islandLocation = island.getCenter(plugin.getSettings().defaultWorldEnvironment);
            islandsByPositions.remove(IslandPosition.of(islandLocation));

            if(plugin.getProviders().hasCustomWorldsSupport()){
                runWithCustomWorld(islandLocation, island, World.Environment.NORMAL,
                        location -> islandsByPositions.remove(IslandPosition.of(location)));
                runWithCustomWorld(islandLocation, island, World.Environment.NETHER,
                        location -> islandsByPositions.remove(IslandPosition.of(location)));
                runWithCustomWorld(islandLocation, island, World.Environment.THE_END,
                        location -> islandsByPositions.remove(IslandPosition.of(location)));
            }

            islandsByUUID.remove(island.getUniqueId());
        }
        return island;
    }

    public void sort(SortingType sortingType, Runnable onFinish) {
        super.sort(sortingType, ISLANDS_PREDICATE, onFinish);
    }

    public void registerSortingType(SortingType sortingType, boolean sort) {
        super.registerSortingType(sortingType, sort, ISLANDS_PREDICATE);
    }

    public void transferIsland(UUID oldOwner, UUID newOwner){
        Island island = get(oldOwner);
        remove(oldOwner);
        add(newOwner, island);
    }

    private void runWithCustomWorld(Location islandLocation, Island island, World.Environment environment, Consumer<Location> onSuccess){
        try{
            Location location = island.getCenter(environment);
            if(!location.getWorld().equals(islandLocation.getWorld()))
                onSuccess.accept(location);
        }catch (Exception ignored){}
    }

}
