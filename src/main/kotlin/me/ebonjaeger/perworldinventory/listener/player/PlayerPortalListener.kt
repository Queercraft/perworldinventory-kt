package me.ebonjaeger.perworldinventory.listener.player

import me.ebonjaeger.perworldinventory.ConsoleLogger
import me.ebonjaeger.perworldinventory.GroupManager
import me.ebonjaeger.perworldinventory.data.ProfileManager
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerPortalEvent
import javax.inject.Inject

class PlayerPortalListener @Inject constructor(private val groupManager: GroupManager,
                                               private val profileManager: ProfileManager) : Listener
{

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerPortal(event: PlayerPortalEvent)
    {
        val destination = event.to ?: return // Why is it even possible for the destination to be null?

        if (event.isCancelled || event.from.world == destination.world)
        {
            return
        }

        val player = event.player
        val worldFromName = event.from.world!!.name // The server will never provide a null world in a Location
        val worldToName = destination.world!!.name // The server will never provide a null world in a Location
        val groupFrom = groupManager.getGroupFromWorld(worldFromName)
        val groupTo = groupManager.getGroupFromWorld(worldToName)

        ConsoleLogger.fine("onPlayerPortal: '${event.player.name}' going teleporting to another world")
        ConsoleLogger.debug("onPlayerPortal: worldFrom='$worldFromName', worldTo='$worldToName'")

        if (groupFrom == groupTo)
        {
            return
        }

        profileManager.addPlayerProfile(player, groupFrom, player.gameMode)

        // TODO: Save the player's last location

        // Possibly prevents item duping exploit
        player.closeInventory()
    }
}
