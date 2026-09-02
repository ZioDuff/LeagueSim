-- goals_conceded era valorizzato per tutti i titolari; ora MatchSimulationService
-- lo assegna solo al portiere (posizione 'P'), perche' il consumer fantacalcio lo
-- applica solo a quel ruolo. Questa migration azzera il dato storico per gli altri ruoli.
UPDATE player_match_stats pms
SET goals_conceded = 0
FROM player p
WHERE pms.player_id = p.id
  AND p."position" <> 'P'
  AND pms.goals_conceded <> 0;
