package main

import net.minecraft.server.v1_8_R3.EnumParticle
import net.minecraft.server.v1_8_R3.Item
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.*
import org.bukkit.util.Vector
import xyz.xenondevs.particle.ParticleBuilder
import xyz.xenondevs.particle.ParticleEffect
import xyz.xenondevs.particle.data.ParticleData
import xyz.xenondevs.particle.data.color.RegularColor
import xyz.xenondevs.particle.data.texture.ItemTexture
import xyz.xenondevs.particle.data.texture.ParticleTexture
import org.bukkit.inventory.ItemStack
import xyz.xenondevs.particle.data.color.NoteColor
import xyz.xenondevs.particle.data.color.ParticleColor
import xyz.xenondevs.particle.data.texture.BlockTexture
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


object GameState {
    var pets : HashMap<UUID, Entity> = HashMap<UUID, Entity>()
    var isRunning : Boolean = false
    var playerCount : Int = 0
    var timer : Int = 30
    var a = arrayListOf<Int>()

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
        e.player.allowFlight = true
        e.player.sendMessage("Welcome to Hardcore Games!")
        GameState.playerCount++

        //lavaParticles(e)
        //cloudParticles(e)
        //jesusParticles(e)
        //rainboxCurtainParticles(e)
        //noteEffect(e)
        //heartEffect(e)
        helixParticle(e)
        Pet().createPet(e.player, EntityType.SHEEP)
    }

    @EventHandler
    fun onPlayerLeap(e : PlayerToggleFlightEvent) {
        if (e.player.gameMode != GameMode.CREATIVE) {
            e.isCancelled = true // If player is not in creative we cancel flight
            val b : Block = e.player.world.getBlockAt(e.player.location.subtract(0.0,2.0,0.0))
            if (b.type != Material.AIR) {
                val v : Vector = e.player.location.direction.setY(1)
                e.player.velocity = v
                onLeapFire(e)
            }
        }
    }

    fun onLeapFire(e : PlayerToggleFlightEvent) {

        val r : Random = Random()
        val p1 : PacketPlayOutWorldParticles = PacketPlayOutWorldParticles(EnumParticle.FLAME, true, e.player.location.x.toFloat(), e.player.location.y.toFloat(), e.player.location.z.toFloat(), 1f, 1f, 1f, 0.1f, 30)

        for (p : Player in Bukkit.getOnlinePlayers()) {
            (p as CraftPlayer).handle.playerConnection.sendPacket(p1)
        }

    }

    fun onLeapFrost(e : PlayerToggleFlightEvent) {

        val r : Random = Random()
        val p1 : PacketPlayOutWorldParticles = PacketPlayOutWorldParticles(EnumParticle.SNOW_SHOVEL, true, e.player.location.x.toFloat(), e.player.location.y.toFloat(), e.player.location.z.toFloat(), 1f, 1f, 1f, 0.1f, 30)

        for (p : Player in Bukkit.getOnlinePlayers()) {
            (p as CraftPlayer).handle.playerConnection.sendPacket(p1)
        }
    }

    fun cloudParticles(e : PlayerJoinEvent) {

        val r : Random = Random()
        var locCloud = e.player.location
        var locRain = e.player.location

        Bukkit.getScheduler().scheduleSyncRepeatingTask(Loader.instance!!, Runnable {
            locCloud = e.player.location.add(Location(e.player.world, (-0.3 + 0.6*r.nextDouble()), 3.0, (-0.3 + 0.6*r.nextDouble())))
            locRain = e.player.location.add(Location(e.player.world, (-0.2 + 0.4*r.nextDouble()), 2.8, (-0.2 + 0.4*r.nextDouble())))

            val p1 : PacketPlayOutWorldParticles = PacketPlayOutWorldParticles(EnumParticle.CLOUD, true, locCloud.x.toFloat(), locCloud.y.toFloat(), locCloud.z.toFloat(), 0f, 0f, 0f, 0f, 5)
            val p2 : PacketPlayOutWorldParticles = PacketPlayOutWorldParticles(EnumParticle.WATER_DROP, true, locRain.x.toFloat(), locRain.y.toFloat(), locRain.z.toFloat(), 0f, 0f, 0f, 0f, 5)

            (e.player as CraftPlayer).handle.playerConnection.sendPacket(p1)
            (e.player as CraftPlayer).handle.playerConnection.sendPacket(p2)

            for (p : Player in Bukkit.getOnlinePlayers()) {
                (p as CraftPlayer).handle.playerConnection.sendPacket(p1)
                (p as CraftPlayer).handle.playerConnection.sendPacket(p2)
            }

        }, 0, 1)
    }

    fun heartEffect(e : PlayerJoinEvent) {

        var locHeart = e.player.location
        val pb = ParticleBuilder(ParticleEffect.HEART)

        Bukkit.getScheduler().scheduleSyncRepeatingTask(Loader.instance!!, Runnable {

            locHeart = e.player.location.add(Location(e.player.world, 0.0, 2.5, 0.0))
            pb.setLocation(locHeart).display()

        }, 0, 10)
    }

    fun helixParticle(e : PlayerJoinEvent) {
        var locHelix = e.player.location
        val pb = ParticleBuilder(ParticleEffect.FLAME)
        var arc : Double = 0.0
        var height : Double = 0.0
        var up : Boolean = true

        Bukkit.getScheduler().scheduleSyncRepeatingTask(Loader.instance!!, Runnable {

            if (arc < 2.0 * PI) arc += PI/16
            else arc = 0.0

            if (up && height < 2.3) height += 0.071
            else if(up && height >= 2.3) up = false
            else if(!up && height > 0.0) height -= 0.071
            else if(!up && height <= 0.0) up = true

            locHelix = e.player.location.add(Location(e.player.world, cos(arc), height, sin(arc)))
            pb.setLocation(locHelix).display()

        }, 0, 1)
    }

    fun noteEffect(e : PlayerJoinEvent) {

        val r : Random = Random()
        var locCloud = e.player.location
        val pb = ParticleBuilder(ParticleEffect.NOTE)
        var i : Int = 5

        Bukkit.getScheduler().scheduleSyncRepeatingTask(Loader.instance!!, Runnable {

            locCloud = e.player.location.add(Location(e.player.world, 1.0, 1.5, 0.0))
            pb.setLocation(locCloud).setParticleData(NoteColor(i)).setAmount(1).display()

            locCloud = e.player.location.add(Location(e.player.world, 0.0, 1.5, 1.0))
            pb.setLocation(locCloud).setParticleData(NoteColor(i)).setAmount(1).display()

            locCloud = e.player.location.add(Location(e.player.world, -1.0, 1.5, 0.0))
            pb.setLocation(locCloud).setParticleData(NoteColor(i)).setAmount(1).display()

            locCloud = e.player.location.add(Location(e.player.world, 0.0, 1.5, -1.0))
            pb.setLocation(locCloud).setParticleData(NoteColor(i)).setAmount(1).display()

            if (i < 20) i++
            else i = 0

        }, 0, 15)
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

            val p1 : PacketPlayOutWorldParticles = PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true, first.x.toFloat(), first.y.toFloat(), first.z.toFloat(), 0f, 0f, 0f, 0f, 5)
            val p2 : PacketPlayOutWorldParticles = PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true, second.x.toFloat(), second.y.toFloat(), second.z.toFloat(), 0f, 0f, 0f, 0f, 5)

            for (p : Player in Bukkit.getOnlinePlayers()) {
                (p as CraftPlayer).handle.playerConnection.sendPacket(p1)
                (p as CraftPlayer).handle.playerConnection.sendPacket(p2)
            }

        },0 ,1)
    }

    fun rainboxCurtainParticles(e : PlayerJoinEvent) {

        var lin1 : Double = 0.0
        var lin2 : Double = 0.0
        var i : Int = 0
        var j : Int = 0
        var loc = e.player.location
        val r : Random = Random()
        val p1 = ParticleBuilder(ParticleEffect.REDSTONE)

        Bukkit.getScheduler().scheduleSyncRepeatingTask(Loader.instance!!, Runnable {
            if (i < 7) i++
            else i = 0
            if (lin1 >= 2.3) {
                lin1 = 0.0
                i = 0
            }
            else lin1 += 0.1

            loc = e.player.location

            p1.setLocation(loc.clone().add(cos(0.0), 2.3-lin1, sin(0.0))).setParticleData(RegularColor(255,0,0)).setAmount(1).display()
            p1.setLocation(loc.clone().add(cos(PI/4), 2.3-lin1, sin(PI/4))).setParticleData(RegularColor(255,135,0)).setAmount(1).display()
            p1.setLocation(loc.clone().add(cos(2*PI/4), 2.3-lin1, sin(2*PI/4))).setParticleData(RegularColor(255,255,0)).setAmount(1).display()
            p1.setLocation(loc.clone().add(cos(3*PI/4), 2.3-lin1, sin(3*PI/4))).setParticleData(RegularColor(0,255,0)).setAmount(1).display()
            p1.setLocation(loc.clone().add(cos(4*PI/4), 2.3-lin1, sin(4*PI/4))).setParticleData(RegularColor(0,255,255)).setAmount(1).display()
            p1.setLocation(loc.clone().add(cos(5*PI/4), 2.3-lin1, sin(5*PI/4))).setParticleData(RegularColor(0,0,255)).setAmount(1).display()
            p1.setLocation(loc.clone().add(cos(6*PI/4), 2.3-lin1, sin(6*PI/4))).setParticleData(RegularColor(135,0,255)).setAmount(1).display()
            p1.setLocation(loc.clone().add(cos(7*PI/4), 2.3-lin1, sin(7*PI/4))).setParticleData(RegularColor(0,0,0)).setAmount(1).display()
            p1.setLocation(loc.clone().add(cos(0.0), 2.3-lin1, sin(0.0))).setParticleData(RegularColor(255,0,0)).setAmount(1).display()
            p1.setLocation(loc.clone().add(cos(PI/4), 2.3-lin1, sin(PI/4))).setParticleData(RegularColor(255,135,0)).setAmount(1).display()
            p1.setLocation(loc.clone().add(cos(2*PI/4), 2.3-lin1, sin(2*PI/4))).setParticleData(RegularColor(255,255,0)).setAmount(1).display()
            p1.setLocation(loc.clone().add(cos(3*PI/4), 2.3-lin1, sin(3*PI/4))).setParticleData(RegularColor(0,255,0)).setAmount(1).display()
            p1.setLocation(loc.clone().add(cos(4*PI/4), 2.3-lin1, sin(4*PI/4))).setParticleData(RegularColor(0,255,255)).setAmount(1).display()
            p1.setLocation(loc.clone().add(cos(5*PI/4), 2.3-lin1, sin(5*PI/4))).setParticleData(RegularColor(0,0,255)).setAmount(1).display()
            p1.setLocation(loc.clone().add(cos(6*PI/4), 2.3-lin1, sin(6*PI/4))).setParticleData(RegularColor(135,0,255)).setAmount(1).display()
            p1.setLocation(loc.clone().add(cos(7*PI/4), 2.3-lin1, sin(7*PI/4))).setParticleData(RegularColor(0,0,0)).setAmount(1).display()
            p1.setLocation(loc.clone().add(cos(0.0), 2.3-lin1, sin(0.0))).setParticleData(RegularColor(255,0,0)).setAmount(1).display()
            p1.setLocation(loc.clone().add(cos(PI/4), 2.3-lin1, sin(PI/4))).setParticleData(RegularColor(255,135,0)).setAmount(1).display()
            p1.setLocation(loc.clone().add(cos(2*PI/4), 2.3-lin1, sin(2*PI/4))).setParticleData(RegularColor(255,255,0)).setAmount(1).display()
            p1.setLocation(loc.clone().add(cos(3*PI/4), 2.3-lin1, sin(3*PI/4))).setParticleData(RegularColor(0,255,0)).setAmount(1).display()
            p1.setLocation(loc.clone().add(cos(4*PI/4), 2.3-lin1, sin(4*PI/4))).setParticleData(RegularColor(0,255,255)).setAmount(1).display()
            p1.setLocation(loc.clone().add(cos(5*PI/4), 2.3-lin1, sin(5*PI/4))).setParticleData(RegularColor(0,0,255)).setAmount(1).display()
            p1.setLocation(loc.clone().add(cos(6*PI/4), 2.3-lin1, sin(6*PI/4))).setParticleData(RegularColor(135,0,255)).setAmount(1).display()
            p1.setLocation(loc.clone().add(cos(7*PI/4), 2.3-lin1, sin(7*PI/4))).setParticleData(RegularColor(0,0,0)).setAmount(1).display()
            p1.setLocation(loc.clone().add(cos(0.0), 2.3-lin1, sin(0.0))).setParticleData(RegularColor(255,0,0)).setAmount(1).display()
            p1.setLocation(loc.clone().add(cos(PI/4), 2.3-lin1, sin(PI/4))).setParticleData(RegularColor(255,135,0)).setAmount(1).display()
            p1.setLocation(loc.clone().add(cos(2*PI/4), 2.3-lin1, sin(2*PI/4))).setParticleData(RegularColor(255,255,0)).setAmount(1).display()
            p1.setLocation(loc.clone().add(cos(3*PI/4), 2.3-lin1, sin(3*PI/4))).setParticleData(RegularColor(0,255,0)).setAmount(1).display()
            p1.setLocation(loc.clone().add(cos(4*PI/4), 2.3-lin1, sin(4*PI/4))).setParticleData(RegularColor(0,255,255)).setAmount(1).display()
            p1.setLocation(loc.clone().add(cos(5*PI/4), 2.3-lin1, sin(5*PI/4))).setParticleData(RegularColor(0,0,255)).setAmount(1).display()
            p1.setLocation(loc.clone().add(cos(6*PI/4), 2.3-lin1, sin(6*PI/4))).setParticleData(RegularColor(135,0,255)).setAmount(1).display()
            p1.setLocation(loc.clone().add(cos(7*PI/4), 2.3-lin1, sin(7*PI/4))).setParticleData(RegularColor(0,0,0)).setAmount(1).display()


            if (2.3-lin1 <= 1.15 || 2.3-lin2 <= 2.1) {
                if (j < 7) j++
                else j = 0
                if (lin2 >= 2.3) {
                    lin2 = 0.0
                    j = 0
                }
                else lin2 += 0.1
                loc = e.player.location

                p1.setLocation(loc.clone().add(cos(0.0), 2.3-lin2, sin(0.0))).setParticleData(RegularColor(255,0,0)).setAmount(1).display()
                p1.setLocation(loc.clone().add(cos(PI/4), 2.3-lin2, sin(PI/4))).setParticleData(RegularColor(255,135,0)).setAmount(1).display()
                p1.setLocation(loc.clone().add(cos(2*PI/4), 2.3-lin2, sin(2*PI/4))).setParticleData(RegularColor(255,255,0)).setAmount(1).display()
                p1.setLocation(loc.clone().add(cos(3*PI/4), 2.3-lin2, sin(3*PI/4))).setParticleData(RegularColor(0,255,0)).setAmount(1).display()
                p1.setLocation(loc.clone().add(cos(4*PI/4), 2.3-lin2, sin(4*PI/4))).setParticleData(RegularColor(0,255,255)).setAmount(1).display()
                p1.setLocation(loc.clone().add(cos(5*PI/4), 2.3-lin2, sin(5*PI/4))).setParticleData(RegularColor(0,0,255)).setAmount(1).display()
                p1.setLocation(loc.clone().add(cos(6*PI/4), 2.3-lin2, sin(6*PI/4))).setParticleData(RegularColor(135,0,255)).setAmount(1).display()
                p1.setLocation(loc.clone().add(cos(7*PI/4), 2.3-lin2, sin(7*PI/4))).setParticleData(RegularColor(0,0,0)).setAmount(1).display()
                p1.setLocation(loc.clone().add(cos(0.0), 2.3-lin2, sin(0.0))).setParticleData(RegularColor(255,0,0)).setAmount(1).display()
                p1.setLocation(loc.clone().add(cos(PI/4), 2.3-lin2, sin(PI/4))).setParticleData(RegularColor(255,135,0)).setAmount(1).display()
                p1.setLocation(loc.clone().add(cos(2*PI/4), 2.3-lin2, sin(2*PI/4))).setParticleData(RegularColor(255,255,0)).setAmount(1).display()
                p1.setLocation(loc.clone().add(cos(3*PI/4), 2.3-lin2, sin(3*PI/4))).setParticleData(RegularColor(0,255,0)).setAmount(1).display()
                p1.setLocation(loc.clone().add(cos(4*PI/4), 2.3-lin2, sin(4*PI/4))).setParticleData(RegularColor(0,255,255)).setAmount(1).display()
                p1.setLocation(loc.clone().add(cos(5*PI/4), 2.3-lin2, sin(5*PI/4))).setParticleData(RegularColor(0,0,255)).setAmount(1).display()
                p1.setLocation(loc.clone().add(cos(6*PI/4), 2.3-lin2, sin(6*PI/4))).setParticleData(RegularColor(135,0,255)).setAmount(1).display()
                p1.setLocation(loc.clone().add(cos(7*PI/4), 2.3-lin2, sin(7*PI/4))).setParticleData(RegularColor(0,0,0)).setAmount(1).display()
                p1.setLocation(loc.clone().add(cos(0.0), 2.3-lin2, sin(0.0))).setParticleData(RegularColor(255,0,0)).setAmount(1).display()
                p1.setLocation(loc.clone().add(cos(PI/4), 2.3-lin2, sin(PI/4))).setParticleData(RegularColor(255,135,0)).setAmount(1).display()
                p1.setLocation(loc.clone().add(cos(2*PI/4), 2.3-lin2, sin(2*PI/4))).setParticleData(RegularColor(255,255,0)).setAmount(1).display()
                p1.setLocation(loc.clone().add(cos(3*PI/4), 2.3-lin2, sin(3*PI/4))).setParticleData(RegularColor(0,255,0)).setAmount(1).display()
                p1.setLocation(loc.clone().add(cos(4*PI/4), 2.3-lin2, sin(4*PI/4))).setParticleData(RegularColor(0,255,255)).setAmount(1).display()
                p1.setLocation(loc.clone().add(cos(5*PI/4), 2.3-lin2, sin(5*PI/4))).setParticleData(RegularColor(0,0,255)).setAmount(1).display()
                p1.setLocation(loc.clone().add(cos(6*PI/4), 2.3-lin2, sin(6*PI/4))).setParticleData(RegularColor(135,0,255)).setAmount(1).display()
                p1.setLocation(loc.clone().add(cos(7*PI/4), 2.3-lin2, sin(7*PI/4))).setParticleData(RegularColor(0,0,0)).setAmount(1).display()
                p1.setLocation(loc.clone().add(cos(0.0), 2.3-lin2, sin(0.0))).setParticleData(RegularColor(255,0,0)).setAmount(1).display()
                p1.setLocation(loc.clone().add(cos(PI/4), 2.3-lin2, sin(PI/4))).setParticleData(RegularColor(255,135,0)).setAmount(1).display()
                p1.setLocation(loc.clone().add(cos(2*PI/4), 2.3-lin2, sin(2*PI/4))).setParticleData(RegularColor(255,255,0)).setAmount(1).display()
                p1.setLocation(loc.clone().add(cos(3*PI/4), 2.3-lin2, sin(3*PI/4))).setParticleData(RegularColor(0,255,0)).setAmount(1).display()
                p1.setLocation(loc.clone().add(cos(4*PI/4), 2.3-lin2, sin(4*PI/4))).setParticleData(RegularColor(0,255,255)).setAmount(1).display()
                p1.setLocation(loc.clone().add(cos(5*PI/4), 2.3-lin2, sin(5*PI/4))).setParticleData(RegularColor(0,0,255)).setAmount(1).display()
                p1.setLocation(loc.clone().add(cos(6*PI/4), 2.3-lin2, sin(6*PI/4))).setParticleData(RegularColor(135,0,255)).setAmount(1).display()
                p1.setLocation(loc.clone().add(cos(7*PI/4), 2.3-lin2, sin(7*PI/4))).setParticleData(RegularColor(0,0,0)).setAmount(1).display()

            }
        },0 ,5)
    }

    fun jesusParticles(e : PlayerJoinEvent) {

        var arc : Double = 0.0
        var loc = e.player.location

        var first : Location = e.player.location
        var second : Location = e.player.location
        var third : Location = e.player.location
        var fourth : Location = e.player.location

        Bukkit.getScheduler().scheduleSyncRepeatingTask(Loader.instance!!, Runnable {
            arc += PI / 16
            loc = e.player.location
            first = loc.clone().add(cos(arc)*0.4,   2.7, 0.4*sin(arc))
            second = loc.clone().add(cos(arc + PI*1/2)*0.4,   2.7, 0.4*sin(arc + PI*1/2))
            third = loc.clone().add(cos(arc + PI)*0.4,   2.7, 0.4*sin(arc + PI))
            fourth = loc.clone().add(cos(arc + PI*3/2)*0.4,   2.7, 0.4*sin(arc + PI*3/2))

            val p1 : PacketPlayOutWorldParticles = PacketPlayOutWorldParticles(EnumParticle.CRIT, true, first.x.toFloat(), first.y.toFloat(), first.z.toFloat(), 0f, 0f, 0f, 0f, 1)
            val p2 : PacketPlayOutWorldParticles = PacketPlayOutWorldParticles(EnumParticle.CRIT, true, second.x.toFloat(), second.y.toFloat(), second.z.toFloat(), 0f, 0f, 0f, 0f, 1)
            val p3 : PacketPlayOutWorldParticles = PacketPlayOutWorldParticles(EnumParticle.CRIT, true, third.x.toFloat(), third.y.toFloat(), third.z.toFloat(), 0f, 0f, 0f, 0f, 1)
            val p4 : PacketPlayOutWorldParticles = PacketPlayOutWorldParticles(EnumParticle.CRIT, true, fourth.x.toFloat(), fourth.y.toFloat(), fourth.z.toFloat(), 0f, 0f, 0f, 0f, 1)

            for (p : Player in Bukkit.getOnlinePlayers()) {
                (p as CraftPlayer).handle.playerConnection.sendPacket(p1)
                (p as CraftPlayer).handle.playerConnection.sendPacket(p2)
                (p as CraftPlayer).handle.playerConnection.sendPacket(p3)
                (p as CraftPlayer).handle.playerConnection.sendPacket(p4)
            }

        },0 ,1)
    }

    @EventHandler
    fun onRunParticles(e : PlayerMoveEvent) {


        val r : Random = Random()

        for (i in 0..4) {

            val p1 : PacketPlayOutWorldParticles = PacketPlayOutWorldParticles(EnumParticle.CRIT_MAGIC, true, (e.player.location.x + r.nextDouble()*0.5).toFloat(), (e.player.location.y + r.nextDouble()*0.5).toFloat(), (e.player.location.z + r.nextDouble()*0.5).toFloat(), 0f, 0f, 0f, 0f, 1)
            val p2 : PacketPlayOutWorldParticles = PacketPlayOutWorldParticles(EnumParticle.CRIT_MAGIC, true, (e.player.location.x - r.nextDouble()*0.5).toFloat(), (e.player.location.y + r.nextDouble()*0.5).toFloat(), (e.player.location.z - r.nextDouble()*0.5).toFloat(), 0f, 0f, 0f, 0f, 1)

            for (p : Player in Bukkit.getOnlinePlayers()) {
                (p as CraftPlayer).handle.playerConnection.sendPacket(p1)
                (p as CraftPlayer).handle.playerConnection.sendPacket(p2)
            }
        }
    }

    @EventHandler
    fun onPlayerQuit(e : PlayerQuitEvent) {
        GameState.playerCount--
        if (GameState.pets.containsKey(e.player.uniqueId)) {
            GameState.pets[e.player.uniqueId]?.remove()

        }
    }

    @EventHandler
    fun onDMG(e : EntityDamageEvent) {
        if (GameState.pets.containsValue(e.entity)) e.isCancelled = true
        //if (GameState.pets.containsValue(e.damager)) e.isCancelled = true
    }

    @EventHandler
    fun onPlayerInteract(e : PlayerInteractEvent) {
        if(!GameState.isRunning) e.isCancelled = false
    }

    @EventHandler
    fun playerAttackEvent(e : EntityDamageEvent) {
        if(!GameState.isRunning) e.isCancelled = false
    }

    @EventHandler
    fun onMove(e : PlayerMoveEvent) {
        if (GameState.pets.containsKey(e.player.uniqueId)) {
            if (GameState.pets.get(e.player.uniqueId)?.location!!.distance(e.player.location) < 10) {
                Pet().walkToLocation(GameState.pets[e.player.uniqueId] as Creature, e.player.location, 1.8)
            } else {
                GameState.pets.get(e.player.uniqueId)?.teleport(e.player.location)
            }
        }
    }

}