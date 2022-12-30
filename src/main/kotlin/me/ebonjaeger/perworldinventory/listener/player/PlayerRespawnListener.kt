package me.ebonjaeger.perworldinventory.listener.player

import me.ebonjaeger.perworldinventory.ConsoleLogger
import me.ebonjaeger.perworldinventory.GroupManager
import me.ebonjaeger.perworldinventory.data.ProfileManager
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerTeleportEvent
import javax.inject.Inject

class PlayerRespawnListener @Inject constructor(private val groupManager: GroupManager,
                                                 private val profileManager: ProfileManager) : Listener
{

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerRespawn(event: PlayerRespawnEvent)
    {
        val destination = event.respawnLocation

        if (event.player.location.world == destination.world)
        {
            return
        }

        val player = event.player
        val worldFromName = player.location.world!!.name // The server will never provide a null world in a Location
        val worldToName = destination.world!!.name // The server will never provide a null world in a Location
        val groupFrom = groupManager.getGroupFromWorld(worldFromName)
        val groupTo = groupManager.getGroupFromWorld(worldToName)

        ConsoleLogger.fine("onPlayerRespawn: '${event.player.name}' going teleporting to another world")
        ConsoleLogger.debug("onPlayerRespawn: worldFrom='$worldFromName', worldTo='$worldToName'")

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
