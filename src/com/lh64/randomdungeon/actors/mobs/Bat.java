/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.lh64.randomdungeon.actors.mobs;

import java.util.HashSet;

import com.lh64.randomdungeon.Dungeon;
import com.lh64.randomdungeon.actors.Char;
import com.lh64.randomdungeon.effects.Speck;
import com.lh64.randomdungeon.items.potions.PotionOfHealing;
import com.lh64.randomdungeon.items.weapon.enchantments.Leech;
import com.lh64.randomdungeon.sprites.BatSprite;
import com.lh64.utils.Random;

public class Bat extends Mob {

	{
		name = "vampire bat";
		spriteClass = BatSprite.class;
		
		if(Dungeon.hero.lvl <= 6){
			HP = HT = Random.Int((Dungeon.hero.lvl /3 +1)*5 +4,(Dungeon.hero.lvl /3 +1)*6 +5);
			} else {
				HP = HT = Random.Int((Dungeon.hero.lvl /3 +1)*8 +4,(Dungeon.hero.lvl /3 +1)*10 +5);
			}
		defenseSkill = (Dungeon.hero.lvl/3 +1) + 2;
		baseSpeed = 2f;
		
		if(Dungeon.hero.lvl <= 1){
			EXP = Dungeon.hero.lvl;
			} else{
				EXP = Random.Int(Dungeon.hero.lvl/2,Dungeon.hero.lvl);
			}
		maxLvl = 15;
		
		flying = true;
		
		loot = new PotionOfHealing();
		lootChance = 0.125f;
	}
	
	@Override
	public int damageRoll() {
		if(Dungeon.hero.lvl <= 8){
			return Random.Int( (Dungeon.hero.lvl/3) ,(Dungeon.hero.lvl/3 + 1) + 4);
			} else {
			return Random.Int( (Dungeon.hero.lvl/3) +3,(Dungeon.hero.lvl/3)*2 + 5);
			}
	}
	
	@Override
	public int attackSkill( Char target ) {
		return ((Dungeon.hero.lvl/3 +1) * 2) + 7;
	}
	
	@Override
	public int dr() {
		return (Dungeon.hero.lvl /3 +1);
	}
	
	@Override
	public String defenseVerb() {
		return "evaded";
	}
	
	@Override
	public int attackProc( Char enemy, int damage ) {
		
		int reg = Math.min( damage, HT - HP );
		
		if (reg > 0) {
			HP += reg;
			sprite.emitter().burst( Speck.factory( Speck.HEALING ), 1 );
		}
		
		return damage;
	}
	
	@Override
	public String description() {
		return
			"These brisk and tenacious inhabitants of cave domes may defeat much larger opponents by " +
			"replenishing their health with each successful attack.";
	}
	
	private static final HashSet<Class<?>> RESISTANCES = new HashSet<Class<?>>();
	static {
		RESISTANCES.add( Leech.class );
	}
	
	@Override
	public HashSet<Class<?>> resistances() {
		return RESISTANCES;
	}
}
