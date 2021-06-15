package main

import me.libraryaddict.disguise.DisguiseAPI
import me.libraryaddict.disguise.disguisetypes.*
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


class DisguiseCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Console cannot execute this command!")
            return true
        }

        val p : Player = sender as Player

        if (args.size < 2) {
            p.sendMessage("Usage: /disguise [type] [name]")
            return true
        }

        if (args[0].equals("player", ignoreCase = true)) {
            DisguiseAPI.disguiseToAll(p, PlayerDisguise(args[1]).setViewSelfDisguise(false))
        } else if (args[0].equals("mob", ignoreCase = true)) {

            val mobDisguise = MobDisguise(DisguiseType.valueOf(args[1].uppercase()))
            mobDisguise.entity = p
            mobDisguise.startDisguise()

        } else if (args[0].equals("block", ignoreCase = true)) {
            try {
                val m : Material = Material.valueOf("DIRT")

                DisguiseAPI.disguiseToAll(p, MiscDisguise(DisguiseType.FALLING_BLOCK, m.id))
                p.sendMessage("You disguised as ${m.name}!")
            } catch (e : java.lang.IllegalArgumentException) {
                p.sendMessage("§cThat is not a valid material!")
                return true
            }
        }
        return true
    }
}