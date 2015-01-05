package com.sanjay900.wonderland.plots;

import org.bukkit.material.MaterialData;
@SuppressWarnings("deprecation")
public enum WallType {
	
	
	GARDEN(new MaterialData(18,(byte) 0),new MaterialData(18,(byte) 4),new MaterialData(18,(byte) 0),
	new MaterialData(2,(byte) 0),new MaterialData(43,(byte) 0),
	new MaterialData(2,(byte) 0),new MaterialData(43,(byte) 0),
	new MaterialData(3,(byte) 1),new MaterialData(125,(byte) 4)),
			SPOOKY(new MaterialData(98,(byte) 2),new MaterialData(98,(byte) 1),new MaterialData(98,(byte) 1),
					new MaterialData(1,(byte) 6),new MaterialData(98,(byte) 3),
					new MaterialData(1,(byte) 4),new MaterialData(1,(byte) 2),
					new MaterialData(109,(byte) 7),new MaterialData(44,(byte) 13)),
			AZTEC(new MaterialData(24,(byte) 0),new MaterialData(24, (byte)2),new MaterialData(24,(byte) 0),
					new MaterialData(179,(byte) 0),new MaterialData(12,(byte) 1),
					new MaterialData(12,(byte) 0),new MaterialData(179,(byte) 0),
					new MaterialData(179,(byte) 2),new MaterialData(179,(byte) 1)),
			WOOD(new MaterialData(5,(byte) 4),new MaterialData(5,(byte) 2),new MaterialData(5,(byte) 1),
					new MaterialData(126,(byte) 12),new MaterialData(126,(byte) 13),
					new MaterialData(126,(byte) 11),new MaterialData(126,(byte) 8),
					new MaterialData(126,(byte) 10),new MaterialData(126,(byte) 9)),
			CAVE(new MaterialData(88,(byte) 0),new MaterialData(43,(byte) 6),new MaterialData(88,(byte) 0),
					new MaterialData(44,(byte) 14),new MaterialData(43,(byte) 14),
					new MaterialData(153,(byte) 0),new MaterialData(44,(byte) 14),
					new MaterialData(114,(byte) 7),new MaterialData(87,(byte) 0));
	public MaterialData wall1;
	public MaterialData wall2;
	public MaterialData wall3;
	public MaterialData floor1;
	public MaterialData floor2;
	public MaterialData floor3;
	public MaterialData floor4;
	public MaterialData floorsign;
	public MaterialData floorbreak;
	
	WallType(MaterialData wall1, MaterialData wall2, MaterialData wall3, MaterialData floor1,
			MaterialData floor2, MaterialData floor3, MaterialData floor4,
			MaterialData floorsign, MaterialData floorbreak) {
		this.wall1 = wall1;
		this.wall2 = wall2;
		this.wall3 = wall3;
		this.floor1 = floor1;
		this.floor2 = floor2;
		this.floor3 = floor3;
		this.floor4 = floor4;
		this.floorsign = floorsign;
		this.floorbreak = floorbreak;
	}
	public MaterialData[] getBlocks() {
		return new MaterialData[]{wall1,wall2,wall3,floor1,floor2,floor3,floor4,floorsign,floorbreak};
	}
}
