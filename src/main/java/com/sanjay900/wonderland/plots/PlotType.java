package com.sanjay900.wonderland.plots;

import lombok.Getter;

import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import com.sanjay900.puzzleapi.api.PlotGame;

@Getter
public enum PlotType implements com.sanjay900.puzzleapi.api.PlotType{
	GARDEN(new MaterialData(Material.LEAVES,(byte) 0),new MaterialData(Material.EMERALD_BLOCK.getId(),(byte) 0),
			new MaterialData(0,(byte) 0),new MaterialData(Material.GRASS,(byte) 0)),
			SPOOKY(new MaterialData(98,(byte) 1),new MaterialData(1,(byte) 6)),
			AZTEC(new MaterialData(24,(byte) 2),new MaterialData(179,(byte) 0)),
			WOOD(new MaterialData(5,(byte) 2),new MaterialData(5,(byte) 2),new MaterialData(0,(byte) 0),new MaterialData(126,(byte) 12)),
			CAVE(new MaterialData(Material.NETHERRACK,(byte) 0),new MaterialData(44,(byte) 14)),
			EMPTY(new MaterialData(5,(byte) 2),new MaterialData(5,(byte) 2),new MaterialData(0,(byte) 0),new MaterialData(126,(byte) 12));

	private MaterialData innerWall;
	private MaterialData outerWall;
	private MaterialData roof;
	private MaterialData floor;
	private PlotGame game = new WonderlandGame();
	PlotType(MaterialData wall, MaterialData floor) {
		this(wall,wall,wall,floor);
	}
	PlotType(MaterialData innerwall, MaterialData outerwall, MaterialData roof, MaterialData floor) {
		this.innerWall=innerwall;
		this.outerWall=outerwall;
		this.roof=roof;
		this.floor=floor;
	}
	@Override
	public String getName() {
		return this.name();
	}
}
