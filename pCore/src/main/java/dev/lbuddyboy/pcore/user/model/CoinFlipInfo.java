package dev.lbuddyboy.pcore.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CoinFlipInfo {

    private int wins = 0, losses = 0;
    private double profit = 0;


}
