-- I rating salvati prima dell'introduzione dell'arrotondamento a step di 0.5
-- (MatchSimulationService.computeRating) sono stati calcolati con precisione
-- a 2 decimali. Questa migration riallinea i dati storici allo stesso formato,
-- arrotondando ogni voto al multiplo di 0.5 piu' vicino.
UPDATE player_match_stats
SET rating = ROUND(rating * 2, 0) / 2;
