package main

import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

public class Loader : JavaPlugin(), Listener{

    override fun onEnable() {
        super.onEnable()
        println("Hello World!")
    }

    override fun onDisable() {
        super.onDisable()
    }
}