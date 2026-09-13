# LeagueSim - project completo

Questo file e' il contesto unico del progetto LeagueSim. Sostituisce le note
separate precedenti (`PROJECT_CONTEXT.md` e `CLAUDE.md`) e va usato come fonte
principale quando si lavora su questo repository o quando si integra LeagueSim
con il progetto fantacalcio/fantafootball.

## Scopo del progetto

LeagueSim e' un microservizio Spring Boot che simula un campionato di calcio e
produce dati consumabili da un'app di fantacalcio: calendario, giornate,
risultati, voti e statistiche per giocatore.

Non ha frontend, template HTML o login utente. Espone solo API REST sotto
`/api/**`, pensate per traffico service-to-service. Il consumer previsto e' un
altro servizio Spring Boot, cioe' il progetto fantacalcio/fantafootball.

## Stack

- Java 26
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA
- PostgreSQL
- Flyway
- Spring Security stateless
- Spring Scheduling
- Maven

Il progetto usa `spring.jpa.hibernate.ddl-auto=validate`: lo schema deve essere
gestito da Flyway, non da Hibernate.

## Configurazione

File principale: `src/main/resources/application.properties`.

Configurazioni attuali:

```properties
spring.application.name=LeagueSim
spring.datasource.url=${LEAGUESIM_DB_URL:jdbc:postgresql://localhost:5432/leaguesim}
spring.datasource.username=${DB_USER:postgresMaster}
spring.datasource.password=${DB_PASS:goPostgresGo}
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.open-in-view=false
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
leaguesim.security.api-key=${LEAGUESIM_API_KEY}
leaguesim.simulation.cron=${LEAGUESIM_SIMULATION_CRON:0 22 15 * * *}
leaguesim.simulation.zone=${LEAGUESIM_SIMULATION_ZONE:Europe/Rome}
```

Nota importante: `LEAGUESIM_API_KEY` non ha default. L'app deve riceverla
dall'ambiente, altrimenti la configurazione non e' valida.

Il cron Spring ha 6 campi (`sec min ora giorno mese giorno-settimana`). Il
default attuale simula ogni giorno alle 15:22 in zona `Europe/Rome`.

## Autenticazione e sicurezza

Tutte le rotte `/api/**` richiedono header:

```http
X-API-KEY: <valore di LEAGUESIM_API_KEY>
```

La sicurezza e' definita da:

- `config/ApiKeyAuthFilter`
- `config/SecurityConfig`

`ApiKeyAuthFilter` confronta la chiave con `MessageDigest.isEqual` e, se e'
corretta, crea un principal tecnico fisso:

- principal: `fantacalcio-service`
- ruolo: `ROLE_SERVICE`

Non esistono utenti finali, sessioni, ruoli admin/read-only o login. Tutte le
API sotto `/api/**`, comprese quelle admin, usano la stessa API key.

`SecurityConfig`:

- disabilita CSRF
- usa sessioni stateless
- richiede autenticazione su `/api/**`
- nega tutto cio' che non e' `/api/**`

## Modello di dominio

Le entita' JPA stanno in `src/main/java/org/generation/italy/LeagueSim/domain`.

- `Team`: nome univoco e `strength` da 1 a 100. La forza pesa la simulazione.
- `Player`: nome, cognome, squadra, numero maglia, posizione, prezzo e flag
  `injured`.
- `Position`: enum fantacalcio italiano con valori `P`, `D`, `C`, `A`.
- `Matchday`: numero giornata, data e flag `closed`.
- `Match`: giornata, squadra casa, squadra trasferta, gol e flag `played`.
- `PlayerMatchStats`: riga statistica di un giocatore in una partita.

Le entita' seguono il pattern locale:

- costruttore protetto no-arg per Hibernate
- costruttore pubblico di dominio
- niente setter generici
- mutazioni esplicite come `Match.recordResult()` e `Matchday.markClosed()`
- sequenze Postgres dedicate con `allocationSize=1`

## Schema e seed dati

Le migrazioni Flyway stanno in `src/main/resources/db/migration`.

- `V1__init_schema.sql`: crea schema, tabelle, sequenze, vincoli e FK.
- `V2__seed_data.sql`: seed storico/demo con 4 squadre.
- `V3__round_existing_ratings_to_half_point.sql`: riallinea i voti storici a
  step di 0.5.
- `V4__clear_goals_conceded_for_non_goalkeepers.sql`: azzera i gol subiti per
  i non portieri.
- `V5__serie_a_realistic_seed.sql`: resetta dati, calendario e statistiche e
  inserisce il seed attuale Serie A.

Il seed attuale effettivo e' quello di `V5`: 20 squadre di Serie A, ciascuna
con 25 giocatori:

- 3 portieri
- 8 difensori
- 8 centrocampisti
- 6 attaccanti

Squadre del seed attuale:

- Napoli
- Inter
- Juventus
- Milan
- Atalanta
- Roma
- Bologna
- Lazio
- Fiorentina
- Como
- Torino
- Udinese
- Genoa
- Cagliari
- Hellas Verona
- Lecce
- Parma
- Sassuolo
- Pisa
- Cremonese

`V5` cancella `player_match_stats`, `match`, `matchday`, `player` e `team`.
Dopo questa migrazione, `StartupFixtureRunner` rigenera il calendario al primo
avvio perche' la tabella `matchday` risulta vuota.

## Generazione calendario

Classe: `service/FixtureGenerationService`.

`generateSeasonIfEmpty()`:

- non fa nulla se esiste almeno una giornata
- legge tutte le squadre dal DB
- se ci sono meno di 2 squadre non genera nulla
- costruisce un girone andata/ritorno con il metodo del cerchio
- crea la prima giornata a `LocalDate.now().plusWeeks(1)`
- distanzia le giornate successive di una settimana

Con 20 squadre vengono generate 38 giornate.

Attenzione: non richiamare questa logica per "rigenerare" la stagione se il DB
contiene gia' giornate. La guardia su `matchdayRepository.count()` serve a
evitare duplicazioni e conflitti con `uq_matchday_number`.

## Motore di simulazione

Classe: `service/MatchSimulationService`.

E' il motore puro della singola partita. Non accede al DB. Riceve:

- squadra casa
- squadra trasferta
- rosa casa
- rosa trasferta

Seleziona 11 titolari per squadra:

- 1 `P`
- 4 `D`
- 3 `C`
- 3 `A`

Esclude i giocatori infortunati. Se mancano giocatori in qualche ruolo, completa
con i disponibili piu' costosi.

La simulazione usa:

- Poisson per il numero di occasioni/gol attesi
- vantaggio casa
- rapporto tra `strength` delle squadre
- pesi per ruolo su gol, assist e autogol
- probabilita' per rigori, parate rigore, cartellini e autogol

Produce un `SimulationOutcome` con:

- gol casa
- gol trasferta
- righe statistiche casa
- righe statistiche trasferta

Ogni `PlayerStatLine` contiene voto, gol, assist, autogol, rigori, clean sheet,
cartellini, gol subiti e flag `starter`.

I voti sono limitati tra 4.0 e 10.0 e arrotondati a step di 0.5.

Nota sul campo `goalsConceded`: viene valorizzato solo per i portieri. Per gli
altri ruoli resta 0, perche' il consumer fantacalcio applica il malus gol subiti
solo al portiere.

## Simulazione giornata

Classe: `service/MatchdaySimulationService`.

`simulate(int matchdayNumber)`:

- cerca la giornata per numero
- per ogni match della giornata non ancora giocato:
  - carica le due rose
  - chiama `MatchSimulationService`
  - salva il risultato sul match
  - salva le statistiche giocatore
- marca la giornata come `closed`

La logica e' transazionale.

Idempotenza pratica: se un match e' gia' `played`, viene saltato. Richiamare la
simulazione sulla stessa giornata non risimula le partite gia' giocate, ma
marca comunque la giornata come chiusa.

## Scheduler

Classe: `service/MatchdaySimulationScheduler`.

`simulateDueMatchdays()` gira secondo:

```properties
leaguesim.simulation.cron
leaguesim.simulation.zone
```

Trova tutte le giornate con:

- `date <= LocalDate.now()`
- `closed = false`

Poi chiama `MatchdaySimulationService.simulate(number)` per ciascuna. Gli errori
di una giornata vengono loggati e non bloccano le successive.

Da tenere a mente: non c'e' un lock distribuito. Con piu' repliche
dell'applicazione, due istanze potrebbero provare a simulare la stessa giornata
in parallelo.

## API REST

Tutte le API richiedono `X-API-KEY`.

### Giocatori

Controller: `web/PlayerController`.

- `GET /api/players`
  - ritorna tutti i giocatori con nome squadra
- `GET /api/players/{id}`
  - ritorna il dettaglio di un giocatore
  - se non esiste: `404 NOT_FOUND`

DTO: `web/dto/PlayerDto`.

Campi:

- `id`
- `firstName`
- `lastName`
- `realTeamName`
- `shirtNumber`
- `position`
- `price`
- `injured`

### Giornate

Controller: `web/MatchdayController`.

- `GET /api/matchdays`
  - ritorna le giornate ordinate per numero
- `GET /api/matchdays/{number}/results`
  - ritorna le statistiche giocatore per la giornata
  - se la giornata non esiste: `404 NOT_FOUND`

DTO:

- `MatchdayDto`
- `PlayerMatchResultDto`

`PlayerMatchResultDto` espone solo `playerId`, non nome giocatore o squadra. Il
consumer deve incrociare i dati con `/api/players`.

### Simulazione admin

Controller: `web/AdminSimulationController`.

- `POST /api/admin/matchdays/{number}/simulate`
  - simula la giornata
  - ritorna `MatchdayDto`
  - se la giornata non esiste: `404 NOT_FOUND`

Non c'e' un ruolo admin separato: basta la stessa API key service-to-service.

## Repository

Repository principali:

- `TeamRepository`
- `PlayerRepository`
- `MatchdayRepository`
- `MatchRepository`
- `PlayerMatchStatsRepository`

`PlayerRepository` usa query `join fetch` per evitare problemi su `team` quando
i DTO leggono il nome squadra.

`MatchdayRepository` espone `findByDateLessThanEqualAndClosedFalse`, usato dallo
scheduler.

`PlayerMatchStatsRepository` espone `findByMatch_Matchday`, usato dai risultati
di giornata.

## Convenzioni di codice

- Controller sottili.
- Controller con repository diretto solo per letture semplici.
- Logica di simulazione dentro i service.
- Scheduler sottile: deve restare un trigger, non contenere business logic.
- DTO come `record` in `web.dto`.
- Factory statiche `from(entity)` per i DTO.
- Entita' JPA non esposte direttamente dalle API.
- Errori "non trovato" via `ResponseStatusException(NOT_FOUND, ...)`.
- Nuove modifiche allo schema sempre con una nuova migration Flyway `V{n}__...sql`.

## Test

Test presenti:

- `LeagueSimApplicationTests`: carica il contesto Spring.
- `MatchSimulationServiceTest`: verifica ripetutamente che la simulazione produca
  11 titolari per squadra, statistiche coerenti, gol subiti solo ai portieri e
  voti nel range atteso.

Copertura mancante o ancora utile:

- test DB/integration per `MatchdaySimulationService`
- test per `MatchdaySimulationScheduler`
- test controller/security sulle API key
- test su generazione calendario con 20 squadre

## Integrazione con fantacalcio/fantafootball

LeagueSim va eseguito come servizio separato con DB PostgreSQL proprio.

Il servizio consumer deve configurare:

- base URL di LeagueSim
- stesso valore segreto di `LEAGUESIM_API_KEY`
- header `X-API-KEY` su ogni chiamata

Flusso tipico:

1. Fantacalcio legge `/api/players` e crea/mantiene il mapping dei calciatori.
2. Fantacalcio legge `/api/matchdays` per conoscere calendario e stato.
3. LeagueSim simula automaticamente le giornate scadute con lo scheduler, oppure
   fantacalcio richiama `POST /api/admin/matchdays/{number}/simulate`.
4. Fantacalcio legge `/api/matchdays/{number}/results`.
5. Fantacalcio incrocia `playerId` con i giocatori e calcola i punteggi.

Identificatori stabili lato integrazione:

- `Player.id`
- `Team.name`
- `Matchday.number`

Non esiste al momento un endpoint aggregato per classifica, storico completo o
risultati partita con nomi squadre.

## Cose che non ci sono

- Nessun frontend.
- Nessun login utente.
- Nessun ruolo read-only.
- Nessun endpoint per creare/modificare squadre.
- Nessun endpoint per creare/modificare giocatori.
- Nessun endpoint per gestire infortuni.
- Nessun endpoint per riaprire una giornata chiusa.
- Nessun endpoint per correggere un risultato.
- Nessuna paginazione su `/api/players` o `/api/matchdays`.
- Nessuna configurazione CORS esplicita.
- Nessuna gestione multi-stagione.
- Nessun lock distribuito per scheduler su piu' repliche.

## Decisioni aperte

- Decidere se la simulazione giornata deve essere comandata solo da LeagueSim
  tramite scheduler o anche dal progetto fantacalcio tramite endpoint admin.
- Decidere se servono endpoint read-only separati o una API key diversa per
  operazioni admin.
- Decidere se il progetto fantacalcio ha bisogno di dati arricchiti nei risultati
  giornata, per esempio nome giocatore, squadra o posizione dentro
  `PlayerMatchResultDto`.
- Decidere se introdurre gestione stagione/campionato per non legare il sistema
  a una sola stagione generata nel DB.
- Decidere se aggiungere lock o vincoli applicativi per evitare simulazioni
  concorrenti in ambienti con piu' istanze.
