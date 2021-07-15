package main

import org.bukkit.Location
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftCreature

import org.bukkit.entity.Creature
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import java.util.*

class Pet() {

    fun createPet(p : Player, type : EntityType) {
        val entity : Entity = p.world.spawnEntity(p.location, type) as Entity
        entity.customName = p.name
        entity.isCustomNameVisible = true
        GameState.pets[p.uniqueId] = entity
    }

    fun walkToLocation(c : Creature, loc : Location, speed : Double) {
        (c as CraftCreature).handle.navigation.a(loc.x, loc.y, loc.z, speed)
    }
}