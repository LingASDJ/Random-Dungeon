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
package com.lh64.randomdungeon.windows;

import android.graphics.RectF;

import com.lh64.gltextures.TextureCache;
import com.lh64.noosa.BitmapText;
import com.lh64.noosa.ColorBlock;
import com.lh64.noosa.Image;
import com.lh64.noosa.audio.Sample;
import com.lh64.randomdungeon.Assets;
import com.lh64.randomdungeon.Dungeon;
import com.lh64.randomdungeon.PixelDungeon;
import com.lh64.randomdungeon.actors.hero.Belongings;
import com.lh64.randomdungeon.actors.hero.Hero;
import com.lh64.randomdungeon.items.Gold;
import com.lh64.randomdungeon.items.Item;
import com.lh64.randomdungeon.items.armor.Armor;
import com.lh64.randomdungeon.items.bags.Bag;
import com.lh64.randomdungeon.items.bags.Keyring;
import com.lh64.randomdungeon.items.bags.ScrollHolder;
import com.lh64.randomdungeon.items.bags.SeedPouch;
import com.lh64.randomdungeon.items.bags.WandHolster;
import com.lh64.randomdungeon.items.wands.Wand;
import com.lh64.randomdungeon.items.weapon.melee.MeleeWeapon;
import com.lh64.randomdungeon.items.weapon.missiles.Boomerang;
import com.lh64.randomdungeon.plants.Plant.Seed;
import com.lh64.randomdungeon.scenes.GameScene;
import com.lh64.randomdungeon.scenes.PixelScene;
import com.lh64.randomdungeon.sprites.ItemSpriteSheet;
import com.lh64.randomdungeon.ui.Icons;
import com.lh64.randomdungeon.ui.ItemSlot;
import com.lh64.randomdungeon.ui.QuickSlot;
import com.lh64.randomdungeon.utils.Utils;
import com.lh64.utils.GameMath;

public class WndBag extends WndTabbed {
	
	public static enum Mode {
		ALL,  
		STORAGE,
		UNIDENTIFED,
		UPGRADEABLE,
		QUICKSLOT,
		FOR_SALE,
		BUY,
		WEAPON,
		ARMOR,
		ENCHANTABLE,
		WAND,
		SEED
	}
	
	protected static final int COLS_P	= 5;
	protected static final int COLS_L	= 5;
	
	protected static final int SLOT_SIZE	= 20;
	protected static final int SLOT_MARGIN	= 1;
	
	protected static final int TAB_WIDTH	= 25;
	
	protected static final int TITLE_HEIGHT	= 12;
	
	private Listener listener;
	private WndBag.Mode mode;
	private String title;
	
	private int nCols;
	private int nRows;
	
	protected int count;
	protected int col;
	protected int row;
	
	private static Mode lastMode;
	public static Bag lastBag;
	
	public WndBag( Bag bag, Listener listener, Mode mode, String title ) {
		
		super();
		
		this.listener = listener;
		this.mode = mode;
		this.title = title;
		
		lastMode = mode;
		lastBag = bag;
		
		nCols = PixelDungeon.landscape() ? COLS_L : COLS_P;
		if(Dungeon.storage==false || Dungeon.ShopkeeperBag == false){
		nRows = (Belongings.BACKPACK_SIZE + 4 + 1) / nCols + ((Belongings.BACKPACK_SIZE + 4 + 1) % nCols > 0 ? 1 : 0);
		} else{
			nRows = (Belongings.BACKPACK_SIZE + 4) / nCols + ((Belongings.BACKPACK_SIZE + 4) % nCols > 0 ? 1 : 0);
		}
		int slotsWidth = SLOT_SIZE * nCols + SLOT_MARGIN * (nCols - 1);
		int slotsHeight = SLOT_SIZE * nRows + SLOT_MARGIN * (nRows -1);
		
		BitmapText txtTitle = PixelScene.createText( title != null ? title : Utils.capitalize( bag.name() ), 9 );
		txtTitle.hardlight( TITLE_COLOR );
		txtTitle.measure();
		txtTitle.x = (int)(slotsWidth - txtTitle.width()) / 2;
		txtTitle.y = (int)(TITLE_HEIGHT - txtTitle.height()) / 2;
		add( txtTitle );
		
		placeItems( bag );
		
		if (Dungeon.storage==true || Dungeon.ShopkeeperBag == true){
			resize( slotsWidth, slotsHeight + TITLE_HEIGHT + 24 );
		} else{
			resize( slotsWidth, slotsHeight + TITLE_HEIGHT );
		}

		
		Belongings stuff = Dungeon.hero.belongings;
		
		Bag[] bags = {
			stuff.backpack, 
			stuff.getItem( SeedPouch.class ), 
			stuff.getItem( ScrollHolder.class ),
			stuff.getItem( WandHolster.class ),
			stuff.getItem( Keyring.class )};
		if(Dungeon.storage == false){
			if(Dungeon.ShopkeeperBag == false){
		for (Bag b : bags) {
			
			if (b != null) {
				BagTab tab = new BagTab( b );
				tab.setSize( TAB_WIDTH, tabHeight() );
				add( tab );
				
				tab.select( b == bag );
				}
				}
				}
			}
		
		
	}
	
	public static WndBag lastBag( Listener listener, Mode mode, String title ) {
		
		if (mode == lastMode && lastBag != null && 
			Dungeon.hero.belongings.backpack.contains( lastBag )) {
			
			return new WndBag( lastBag, listener, mode, title );
			
		} else {
			
			return new WndBag( Dungeon.hero.belongings.backpack, listener, mode, title );
			
		}
	}
	
	public static WndBag seedPouch( Listener listener, Mode mode, String title ) {
		Dungeon.ShopkeeperBag = false;
		SeedPouch pouch = Dungeon.hero.belongings.getItem( SeedPouch.class );
		return pouch != null ?
			new WndBag( pouch, listener, mode, title ) :
			new WndBag( Dungeon.hero.belongings.backpack, listener, mode, title );
	}
	
	
	protected void placeItems( Bag container ) {
		
		// Equipped items
		if(Dungeon.storage == false){
			if(Dungeon.ShopkeeperBag == false){
		Belongings stuff = Dungeon.hero.belongings;
		placeItem( stuff.weapon != null ? stuff.weapon : new Placeholder( ItemSpriteSheet.WEAPON ) );
		placeItem( stuff.armor != null ? stuff.armor : new Placeholder( ItemSpriteSheet.ARMOR ) );
		placeItem( stuff.ring1 != null ? stuff.ring1 : new Placeholder( ItemSpriteSheet.RING ) );
		placeItem( stuff.ring2 != null ? stuff.ring2 : new Placeholder( ItemSpriteSheet.RING ) );
		placeItem( stuff.ring3 != null ? stuff.ring3 : new Placeholder( ItemSpriteSheet.RING  ) );
			}
		}
		boolean backpack = (container == Dungeon.hero.belongings.backpack);
		if (!backpack || !Dungeon.storage == true || !Dungeon.ShopkeeperBag==true) {
			count = nCols;
			col = 0;
			row = 1;
		}
		
		// Items in the bag
		for (Item item : container.items) {
			placeItem( item );
		}
		
		// Free space
		while (count-(backpack ? 4 : nCols) < container.size) {
			placeItem( null );
		}
		
		// Gold in the backpack
		if (container == Dungeon.hero.belongings.backpack) {
			row = nRows - 1;
			col = nCols - 1;
			placeItem( new Gold( Dungeon.gold ) );
		}
	}
	
	protected void placeItem( final Item item ) {
		
		int x = col * (SLOT_SIZE + SLOT_MARGIN);
		int y = TITLE_HEIGHT + row * (SLOT_SIZE + SLOT_MARGIN);
		
		add( new ItemButton( item ).setPos( x, y ) );
		
		if (++col >= nCols) {
			col = 0;
			row++;
		}
		
		count++;
	}
	
	@Override
	public void onMenuPressed() {
		if (listener == null) {
			hide();
			if(mode == Mode.BUY){
				Dungeon.ShopkeeperBag = false;
			}
			else if(mode == Mode.STORAGE){
				Dungeon.storage = false;
			}
		}
	}
	
	@Override
	public void onBackPressed() {
		if (listener != null) {
			listener.onSelect( null );
		}
		super.onBackPressed();
	}
	
	@Override
	protected void onClick( Tab tab ) {
		hide();
		GameScene.show( new WndBag( ((BagTab)tab).bag, listener, mode, title ) );
	}
	
	@Override
	protected int tabHeight() {
		return 20;
	}
	
	private class BagTab extends Tab {
		
		private Image icon;

		private Bag bag;
		
		public BagTab( Bag bag ) {
			super();
			
			this.bag = bag;
			if(mode != Mode.STORAGE || mode != Mode.BUY){
			icon = icon();
			add( icon );
			}
		}
		
		@Override
		protected void select( boolean value ) {
			super.select( value );
			icon.am = selected ? 1.0f : 0.6f;
		}
		
		@Override
		protected void layout() {
			super.layout();
			
			icon.copy( icon() );
			icon.x = x + (width - icon.width) / 2;
			icon.y = y + (height - icon.height) / 2 - 2 - (selected ? 0 : 1);
			if (!selected && icon.y < y + CUT) {
				RectF frame = icon.frame();
				frame.top += (y + CUT - icon.y) / icon.texture.height;
				icon.frame( frame );
				icon.y = y + CUT;
			}
		}
		
		private Image icon() {

			if (bag instanceof SeedPouch) {
				return Icons.get( Icons.SEED_POUCH );
			} else if (bag instanceof ScrollHolder) {
				return Icons.get( Icons.SCROLL_HOLDER );
			} else if (bag instanceof WandHolster) {
				return Icons.get( Icons.WAND_HOLSTER );
			} else if (bag instanceof Keyring) {
				return Icons.get( Icons.KEYRING );
			} else {
				return Icons.get( Icons.BACKPACK );
			}
		}
	}
	
	private static class Placeholder extends Item {		
		{
			name = null;
		}
		
		public Placeholder( int image ) {
			this.image = image;
		}
		
		@Override
		public boolean isIdentified() {
			return true;
		}
		
		@Override
		public boolean isEquipped( Hero hero ) {
			return true;
		}
	}
	
	private class ItemButton extends ItemSlot {
		
		private static final int NORMAL		= 0xFF4A4D44;
		private static final int EQUIPPED	= 0xFF63665B;
		
		private static final int NBARS	= 3;
		
		private Item item;
		private ColorBlock bg;
		
		private ColorBlock durability[];
		
		public ItemButton( Item item ) {
			
			super( item );

			this.item = item;
			if (item instanceof Gold) {
				bg.visible = false;
			}
			
			width = height = SLOT_SIZE;
		}
		
		@Override
		protected void createChildren() {	
			bg = new ColorBlock( SLOT_SIZE, SLOT_SIZE, NORMAL );
			add( bg );
			
			super.createChildren();
		}
		
		@Override
		protected void layout() {
			bg.x = x;
			bg.y = y;
			
			if (durability != null) {
				for (int i=0; i < NBARS; i++) {
					durability[i].x = x + 1 + i * 3;
					durability[i].y = y + height - 3;
				}
			}
			
			super.layout();
		}
		
		@Override
		public void item( Item item ) {
			
			super.item( item );
			if (item != null) {

				bg.texture( TextureCache.createSolid( item.isEquipped( Dungeon.hero ) ? EQUIPPED : NORMAL ) );
				if (item.cursed && item.cursedKnown) {
					bg.ra = +0.2f;
					bg.ga = -0.1f;
				} else if (!item.isIdentified()) {
					bg.ra = 0.1f;
					bg.ba = 0.1f;
				}
				
				if (lastBag.owner.isAlive() && item.isUpgradable() && item.levelKnown) {
					durability = new ColorBlock[NBARS];
					int nBars = (int)GameMath.gate( 0, Math.round( (float)NBARS * item.durability() / item.maxDurability() ), NBARS );
					for (int i=0; i < nBars; i++) {
						durability[i] = new ColorBlock( 2, 2, 0xFF00EE00 );
						add( durability[i] );
					}
					for (int i=nBars; i < NBARS; i++) {
						durability[i] = new ColorBlock( 2, 2, 0xFFCC0000 );
						add( durability[i] );
					}
				}
				
				if (item.name() == null) {
					enable( false );
				} else {
					enable( 
						mode == Mode.QUICKSLOT && (item.defaultAction != null) ||
						mode == Mode.FOR_SALE && (item.price() > 0) && (!item.isEquipped( Dungeon.hero ) || !item.cursed) ||
						mode == Mode.UPGRADEABLE && item.isUpgradable() || 
						mode == Mode.UNIDENTIFED && !item.isIdentified() ||
						mode == Mode.WEAPON && (item instanceof MeleeWeapon || item instanceof Boomerang) ||
						mode == Mode.ARMOR && (item instanceof Armor) ||
						mode == Mode.ENCHANTABLE && (item instanceof MeleeWeapon || item instanceof Boomerang || item instanceof Armor) ||
						mode == Mode.WAND && (item instanceof Wand) ||
						mode == Mode.SEED && (item instanceof Seed) ||
						mode == Mode.ALL||
						mode == Mode.STORAGE ||
						mode == Mode.BUY
					);
				}
			} else {
				bg.color( NORMAL );
			}
		}
		
		@Override
		protected void onTouchDown() {
			bg.brightness( 1.5f );
			Sample.INSTANCE.play( Assets.SND_CLICK, 0.7f, 0.7f, 1.2f );
		};
		
		protected void onTouchUp() {
			bg.brightness( 1.0f );
		};
		
		@Override
		protected void onClick() {
			if (mode == Mode.STORAGE || mode==Mode.BUY){
				
			}
			if (listener != null) {
				
				hide();
				listener.onSelect( item );
				
			} else {
				
				WndBag.this.add( new WndItem( WndBag.this, item ) );
				
			}
		}
		
		@Override
		protected boolean onLongClick() {
			if (mode != Mode.STORAGE || mode != Mode.BUY){
			if (listener == null && item.defaultAction != null) {
				hide();
				QuickSlot.primaryValue = item.stackable ? item.getClass() : item;
				QuickSlot.refresh();
				return true;
			} else {
				return false;
			}
			} else {
				return false;
			}
		}
	}
	
	public interface Listener {
		void onSelect( Item item );
	}
}
