package com.pjatk.turtlegame.models.DTOs;

import com.pjatk.turtlegame.models.Item;
import com.pjatk.turtlegame.models.TurtleBattleHistory;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;

@Getter
@Setter
public class BattleResultDTO {
    List<StringBuilder> battleLog;

    BattleParticipantDTO winner;

    BattleParticipantDTO loser;

    Integer gold;

    Integer gainedRankingPoints;

    Integer lostRankingPoints;

    TurtleBattleHistory turtleBattleHistory;

    HashMap<Item, Integer> rewards = new HashMap<>();
}
