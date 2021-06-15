package main

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin


class Loader : JavaPlugin() {

    companion object {
        var instance : Loader? = null
    }



    override fun onEnable() {
        instance = this

        // Setup PreGame behavior
        Bukkit.getPluginManager().registerEvents(PreGame(), this)
        this.getCommand("adisguise")?.setExecutor(DisguiseCommand())

    }
}