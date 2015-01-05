package com.sanjay900.wonderland.hologram;

import lombok.Getter;
import lombok.Setter;

import com.sanjay900.wonderland.Wonderland;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;

public class Spike
  extends Hologram
{
	@Getter
  int pos = 0;
  @Getter @Setter
  int dir = 1;
  @Getter @Setter
  Long time;
public BukkitTask task;
  
  public Spike(Wonderland plugin, Location location, Long time)
  {
    super(plugin, location,7,0, HologramType.Spike);
    this.time = time;
  }
  
  public void extend()
  {
    if (this.task != null) {
      this.task.cancel();
    }
    this.task = Bukkit.getScheduler().runTaskTimer(this.plugin, new Runnable()
    {
      public void run()
      {
        Spike.this.location = Spike.this.location.getBlock().getLocation().add(0.5D, Spike.this.pos, 0.5D);
        if (Spike.this.dir == 1)
        {
        	Spike.this.pos = ((int)(Spike.this.pos + 0.1D));
          if (Spike.this.pos >= 1) {
            Spike.this.dir = 0;
          }
        }
        else
        {
        	Spike.this.pos = ((int)(Spike.this.pos - 0.1D));
          if (Spike.this.pos <= 0) {
            Spike.this.dir = 2;
          }
        }
        Spike.this.hologram.getBukkitEntity().teleport(Spike.this.location);
      }
    }, 1L, this.time.longValue());
  }
}
