-- Script di sviluppo, NON una migration Flyway: va eseguito a mano quando serve
-- avere subito una giornata pronta da simulare, senza toccare le date a mano.
--
-- Effetto: rimette in calendario TUTTE le giornate a partire da oggi (una al
-- giorno, nello stesso ordine di `number`), riapre tutte le giornate
-- (closed = false) e azzera tutti i risultati/statistiche già generati, cosi'
-- che il motore di simulazione le trovi come "non giocate".
--
-- ATTENZIONE: cancella tutti i risultati e le statistiche esistenti. Usare
-- solo in ambiente di sviluppo.
--
-- Esecuzione (Postgres nel container devPostgres):
--   docker exec -i devPostgres psql -U postgresMaster -d leaguesim < scripts/reset-and-reschedule-matchdays.sql

BEGIN;

DELETE FROM player_match_stats
WHERE match_id IN (SELECT id FROM match);

UPDATE match
SET played = false,
    home_goals = NULL,
    away_goals = NULL;

UPDATE matchday m
SET date = CURRENT_DATE + (sub.rn - 1),
    closed = false
FROM (
    SELECT id, (ROW_NUMBER() OVER (ORDER BY number))::integer AS rn
    FROM matchday
) sub
WHERE m.id = sub.id;

COMMIT;
