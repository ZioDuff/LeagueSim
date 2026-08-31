CREATE SEQUENCE seq_team_id START WITH 1 INCREMENT BY 1;

CREATE TABLE team (
    id       BIGINT DEFAULT nextval('seq_team_id') NOT NULL,
    name     VARCHAR(100) NOT NULL,
    strength INTEGER NOT NULL DEFAULT 50,
    CONSTRAINT team_pkey PRIMARY KEY (id),
    CONSTRAINT uq_team_name UNIQUE (name),
    CONSTRAINT ck_team_strength CHECK (strength BETWEEN 1 AND 100)
);

CREATE SEQUENCE seq_player_id START WITH 1 INCREMENT BY 1;

CREATE TABLE player (
    id           BIGINT DEFAULT nextval('seq_player_id') NOT NULL,
    first_name   VARCHAR(100) NOT NULL,
    last_name    VARCHAR(100) NOT NULL,
    team_id      BIGINT NOT NULL,
    shirt_number INTEGER NOT NULL,
    "position"   CHAR(1) NOT NULL,
    price        INTEGER NOT NULL,
    injured      BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT player_pkey PRIMARY KEY (id),
    CONSTRAINT fk_player_team FOREIGN KEY (team_id) REFERENCES team (id),
    CONSTRAINT ck_player_position CHECK ("position" IN ('P', 'D', 'C', 'A')),
    CONSTRAINT ck_player_price CHECK (price >= 0),
    CONSTRAINT uq_player_team_shirt UNIQUE (team_id, shirt_number)
);

CREATE SEQUENCE seq_matchday_id START WITH 1 INCREMENT BY 1;

CREATE TABLE matchday (
    id     BIGINT DEFAULT nextval('seq_matchday_id') NOT NULL,
    number INTEGER NOT NULL,
    date   DATE NOT NULL,
    closed BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT matchday_pkey PRIMARY KEY (id),
    CONSTRAINT uq_matchday_number UNIQUE (number)
);

CREATE SEQUENCE seq_match_id START WITH 1 INCREMENT BY 1;

CREATE TABLE match (
    id            BIGINT DEFAULT nextval('seq_match_id') NOT NULL,
    matchday_id   BIGINT NOT NULL,
    home_team_id  BIGINT NOT NULL,
    away_team_id  BIGINT NOT NULL,
    home_goals    INTEGER,
    away_goals    INTEGER,
    played        BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT match_pkey PRIMARY KEY (id),
    CONSTRAINT fk_match_matchday FOREIGN KEY (matchday_id) REFERENCES matchday (id),
    CONSTRAINT fk_match_home_team FOREIGN KEY (home_team_id) REFERENCES team (id),
    CONSTRAINT fk_match_away_team FOREIGN KEY (away_team_id) REFERENCES team (id),
    CONSTRAINT ck_match_teams_different CHECK (home_team_id <> away_team_id)
);

CREATE SEQUENCE seq_player_match_stats_id START WITH 1 INCREMENT BY 1;

CREATE TABLE player_match_stats (
    id              BIGINT DEFAULT nextval('seq_player_match_stats_id') NOT NULL,
    player_id       BIGINT NOT NULL,
    match_id        BIGINT NOT NULL,
    rating          NUMERIC(4, 2) NOT NULL,
    goals           INTEGER NOT NULL DEFAULT 0,
    goals_conceded  INTEGER NOT NULL DEFAULT 0,
    own_goals       INTEGER NOT NULL DEFAULT 0,
    assists         INTEGER NOT NULL DEFAULT 0,
    penalty_saved   INTEGER NOT NULL DEFAULT 0,
    penalty_failed  INTEGER NOT NULL DEFAULT 0,
    clean_sheet     BOOLEAN NOT NULL DEFAULT FALSE,
    yellow_cards    INTEGER NOT NULL DEFAULT 0,
    red_card        BOOLEAN NOT NULL DEFAULT FALSE,
    starter         BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT player_match_stats_pkey PRIMARY KEY (id),
    CONSTRAINT fk_pms_player FOREIGN KEY (player_id) REFERENCES player (id),
    CONSTRAINT fk_pms_match FOREIGN KEY (match_id) REFERENCES match (id),
    CONSTRAINT uq_pms_player_match UNIQUE (player_id, match_id)
);
