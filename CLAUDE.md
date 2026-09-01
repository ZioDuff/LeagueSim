# LeagueSim — note per agenti AI

## Stack
- Spring Boot 4.1.1 / Java 26, Spring Data JPA + PostgreSQL, Flyway, Spring Security stateless (no login utente), Spring Scheduling.

## Moduli principali
- `service/FixtureGenerationService` — genera calendario andata/ritorno una sola volta (se `matchday` è vuoto); prima giornata = avvio app +1 settimana, poi +1 settimana per giornata successiva.
- `service/MatchSimulationService` — motore statistico puro (Poisson + pesi per ruolo) di una singola partita: nessun accesso al DB.
- `service/MatchdaySimulationService` — orchestratore transazionale: simula le partite non giocate di una giornata, salva risultati/statistiche, marca la giornata `closed`.
- `service/MatchdaySimulationScheduler` — job `@Scheduled` giornaliero che trova le giornate con `date <= oggi` e `closed=false` e le simula.
- `config/StartupFixtureRunner` — genera il calendario all'avvio se assente.
- `config/ApiKeyAuthFilter` + `SecurityConfig` — autenticazione service-to-service via header `X-API-KEY`.

## Pattern architetturali
- Flusso: Controller → Service (o Repository diretto per sole letture) → JPA. DTO record dedicati per l'esposizione REST.
- LeagueSim è il produttore dei dati; il progetto fantacalcio è un consumer HTTP esterno — nessuna integrazione a DB condiviso.
- Trigger simulazione: automatico (scheduler, data-based) o manuale (`POST /api/admin/matchdays/{number}/simulate`) — entrambi chiamano `MatchdaySimulationService.simulate(number)`: la logica va modificata solo lì.
- Idempotenza garantita da `Match.played` + `Matchday.closed`: richiamare `simulate()` più volte sulla stessa giornata è sicuro.

## Autorizzazioni
- Un solo ruolo tecnico, `ROLE_SERVICE`, assegnato a chiunque presenti l'header `X-API-KEY` corretto (confronto con `MessageDigest.isEqual`).
- Nessuna distinzione tra endpoint di lettura (`/api/matchdays`, `/api/players`) e admin (`/api/admin/**`): stessa API key, stesso accesso. Non esiste un ruolo read-only separato.
- Nessun login utente, sessioni stateless, CSRF disabilitato: pensato solo per traffico service-to-service.

## Configurazioni critiche
- `leaguesim.security.api-key` — nessun default: l'app non parte senza `LEAGUESIM_API_KEY` in ambiente.
- `leaguesim.simulation.cron` / `.zone` — cron Spring a 6 campi (`sec min ora giorno mese giorno-settimana`), default `0 0 23 * * *` / `Europe/Rome`.
- `spring.jpa.hibernate.ddl-auto=validate` — schema governato solo da Flyway (`db/migration`); Hibernate valida, non crea/altera nulla.
- Sequence Postgres dedicata per entità con `allocationSize=1` (niente batching Hibernate sugli ID): mantenere il pattern per nuove entità.

## Convenzioni
- Entità JPA: costruttore protetto no-arg (Hibernate) + costruttore pubblico di dominio, niente setter; mutazioni via metodi intenzionali (`Match.recordResult()`, `Matchday.markClosed()`).
- DTO come `record` in `web.dto`, con factory statico `from(entity)`; i controller non espongono mai le entità JPA.
- `Position` = enum a lettera singola (`P`/`D`/`C`/`A`, terminologia fantacalcio italiana), persistito come `CHAR(1)`.
- Errore "non trovato" nei controller → `ResponseStatusException(NOT_FOUND, ...)`, mai eccezioni custom.

## Da non fare
- Non mettere logica di business dentro `MatchdaySimulationScheduler`: deve restare un trigger sottile che delega a `MatchdaySimulationService`.
- Non introdurre un ruolo "admin" separato senza rivedere `SecurityConfig`: oggi `ROLE_SERVICE` copre già tutto `/api/**`.
- Non richiamare `FixtureGenerationService` per rigenerare la stagione: è a guardia (`count()>0` → skip), un secondo calendario romperebbe `uq_matchday_number`.
- Non modificare lo schema affidandosi a `ddl-auto`: ogni cambio struttura richiede una nuova migration Flyway `V{n}__...sql`.
- Non assumere che le giornate siano datate "oggi" di default: la prima giornata generata è a +1 settimana dal primo avvio dell'app.

## TODO / decisioni aperte
- Nessun lock anti-concorrenza sullo scheduler: con più repliche dell'app, due istanze potrebbero tentare di simulare la stessa giornata in parallelo.
- Nessun endpoint per riaprire una giornata chiusa o correggere un risultato: flusso solo forward.
- Nessun test automatico per `MatchdaySimulationService`/`MatchdaySimulationScheduler` (solo `MatchSimulationServiceTest` sul motore puro); nessun test DB-based nel repo.
