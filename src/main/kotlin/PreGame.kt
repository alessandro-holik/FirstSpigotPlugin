package main

import org.bukkit.Bukkit
import org.bukkit.Effect
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.block.data.type.Bed
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.*
import java.util.*
import java.util.logging.Handler
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object GameState {
    var isRunning : Boolean = false
    var playerCount : Int = 0
    var timer : Int = 30

    fun timer() {
        Bukkit.getScheduler().runTaskTimer(Loader.instance!!, Runnable {
            if (GameState.timer > 30 && GameState.timer % 60 == 0) Bukkit.broadcastMessage("[HARDCORE GAMES] Match starts in ${GameState.timer/60} minutes.")
            else if (GameState.timer in 11..30 && GameState.timer % 10 == 0) Bukkit.broadcastMessage("[HARDCORE GAMES] Match starts in ${GameState.timer} seconds.")
            else if(GameState.timer <= 10) Bukkit.broadcastMessage("[HARDCORE GAMES] Match starts in ${GameState.timer} seconds.")
            GameState.timer--
            if (GameState.timer <= 0) {
                GameState.startGame()
                Bukkit.getScheduler().cancelTasks(Loader.instance!!)
            }
        }, 0, 20)
    }

    fun startGame() {
        Bukkit.broadcastMessage("[HARDCORE GAMES] Match started!")
    }
}

class PreGame : Listener {

    @EventHandler
    fun onPlayerLogin(e : PlayerLoginEvent) {
        if (GameState.playerCount > 20)  {
            e.player.kickPlayer("SERVER IS FULL 20 / 20.")
            GameState.startGame()
        }
    }

    @EventHandler
    fun onPlayerJoin(e : PlayerJoinEvent) {
        e.player.sendMessage("Welcome to Hardcore Games!")
        GameState.playerCount++

        //lavaParticles(e)
        cloudParticles(e)
    }

    fun cloudParticles(e : PlayerJoinEvent) {

        val r : Random = Random()
        var locCloud = e.player.location
        var locRain = e.player.location

        Bukkit.getScheduler().scheduleSyncRepeatingTask(Loader.instance!!, Runnable {
            locCloud = e.player.location.add(Location(e.player.world, (-0.3 + 0.6*r.nextDouble()), 3.0, (-0.3 + 0.6*r.nextDouble())))
            locRain = e.player.location.add(Location(e.player.world, (-0.2 + 0.4*r.nextDouble()), 2.8, (-0.2 + 0.4*r.nextDouble())))

            e.player.world.spawnParticle(Particle.CLOUD, locCloud, 0)
            e.player.world.spawnParticle(Particle.WATER_DROP, locRain, 0)

        }, 0, 1)
    }

    fun lavaParticles(e : PlayerJoinEvent) {

        var arc : Double = 0.0
        var loc = e.player.location

        var first : Location = e.player.location
        var second : Location = e.player.location

        Bukkit.getScheduler().scheduleSyncRepeatingTask(Loader.instance!!, Runnable {
            arc += PI / 16
            loc = e.player.location
            first = loc.clone().add(cos(arc), sin(arc) + 1, sin(arc))
            second = loc.clone().add(cos(arc + PI), sin(arc) + 1, sin(arc + PI))
            e.player.world.spawnParticle(Particle.REDSTONE, first, 0)
            e.player.world.spawnParticle(Particle.REDSTONE, second, 0)
        },0 ,1)
    }

    @EventHandler
    fun onPlayerQuit(e : PlayerQuitEvent) = GameState.playerCount--

    @EventHandler
    fun onPlayerInteract(e : PlayerInteractEvent) {
        if(!GameState.isRunning) e.isCancelled = true
    }

    @EventHandler
    fun playerAttackEvent(e : EntityDamageEvent) {
        if(!GameState.isRunning) e.isCancelled = true
    }

}