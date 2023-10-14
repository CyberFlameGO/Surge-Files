package dev.lbuddyboy.pcore.user.model;

import dev.lbuddyboy.pcore.mines.MineRank;
import dev.lbuddyboy.pcore.mines.PrivateMine;
import dev.lbuddyboy.pcore.util.Cuboid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RankInfo {

    private long rank = 1;
    private double progress = 0;

    public void increaseRank() {
        this.rank++;
    }

    public void increaseProgress(double amount) {
        this.progress += amount;
    }

    public double getNeededProgress() {
        return 350.0 * this.rank;
    }

    public void setRank(PrivateMine mine, long rank) {
        MineRank before = mine.getMineRank();
        this.rank = rank;
        MineRank after = mine.getMineRank();

        if (before != after) {
            mine.setMinePit(mine.getMinePit().clone()
                    .expand(Cuboid.CuboidDirection.North, after.getMineSize())
                    .expand(Cuboid.CuboidDirection.South, after.getMineSize())
                    .expand(Cuboid.CuboidDirection.Down, after.getMineSize() / 2)
                    .expand(Cuboid.CuboidDirection.West, after.getMineSize())
                    .expand(Cuboid.CuboidDirection.East, after.getMineSize()));
            mine.setMinePitBox(mine.getMinePitBox().clone()
                    .expand(Cuboid.CuboidDirection.North, after.getMineSize())
                    .expand(Cuboid.CuboidDirection.South, after.getMineSize())
                    .expand(Cuboid.CuboidDirection.Down, after.getMineSize() / 2)
                    .expand(Cuboid.CuboidDirection.West, after.getMineSize())
                    .expand(Cuboid.CuboidDirection.East, after.getMineSize()));
        }
    }

}
