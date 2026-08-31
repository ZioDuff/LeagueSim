INSERT INTO team (name, strength) VALUES
    ('Vulcano FC', 72),
    ('Marnera Calcio', 65),
    ('Rocca United', 58),
    ('Fenice Sporting', 50);

-- Vulcano FC (strength 72) — 3 P / 6 D / 6 C / 3 A
INSERT INTO player (first_name, last_name, team_id, shirt_number, "position", price, injured) VALUES
    ('Marco', 'Bellini', (SELECT id FROM team WHERE name = 'Vulcano FC'), 1, 'P', 18, FALSE),
    ('Luca', 'Ferraro', (SELECT id FROM team WHERE name = 'Vulcano FC'), 12, 'P', 9, FALSE),
    ('Davide', 'Costa', (SELECT id FROM team WHERE name = 'Vulcano FC'), 22, 'P', 6, FALSE),
    ('Simone', 'Greco', (SELECT id FROM team WHERE name = 'Vulcano FC'), 2, 'D', 14, FALSE),
    ('Andrea', 'Marino', (SELECT id FROM team WHERE name = 'Vulcano FC'), 3, 'D', 12, FALSE),
    ('Matteo', 'Serra', (SELECT id FROM team WHERE name = 'Vulcano FC'), 4, 'D', 11, FALSE),
    ('Alessio', 'Conti', (SELECT id FROM team WHERE name = 'Vulcano FC'), 5, 'D', 9, TRUE),
    ('Fabio', 'De Angelis', (SELECT id FROM team WHERE name = 'Vulcano FC'), 6, 'D', 8, FALSE),
    ('Nicola', 'Ricci', (SELECT id FROM team WHERE name = 'Vulcano FC'), 13, 'D', 7, FALSE),
    ('Riccardo', 'Fontana', (SELECT id FROM team WHERE name = 'Vulcano FC'), 8, 'C', 24, FALSE),
    ('Gabriele', 'Villa', (SELECT id FROM team WHERE name = 'Vulcano FC'), 10, 'C', 32, FALSE),
    ('Lorenzo', 'Barone', (SELECT id FROM team WHERE name = 'Vulcano FC'), 16, 'C', 18, FALSE),
    ('Tommaso', 'Farina', (SELECT id FROM team WHERE name = 'Vulcano FC'), 17, 'C', 15, FALSE),
    ('Federico', 'Rinaldi', (SELECT id FROM team WHERE name = 'Vulcano FC'), 20, 'C', 12, FALSE),
    ('Emanuele', 'Bruno', (SELECT id FROM team WHERE name = 'Vulcano FC'), 21, 'C', 10, FALSE),
    ('Gianluca', 'Moretti', (SELECT id FROM team WHERE name = 'Vulcano FC'), 9, 'A', 45, FALSE),
    ('Cristian', 'Longo', (SELECT id FROM team WHERE name = 'Vulcano FC'), 11, 'A', 38, FALSE),
    ('Alberto', 'Gatti', (SELECT id FROM team WHERE name = 'Vulcano FC'), 19, 'A', 22, FALSE);

-- Marnera Calcio (strength 65) — 3 P / 6 D / 6 C / 3 A
INSERT INTO player (first_name, last_name, team_id, shirt_number, "position", price, injured) VALUES
    ('Stefano', 'Colombo', (SELECT id FROM team WHERE name = 'Marnera Calcio'), 1, 'P', 15, FALSE),
    ('Paolo', 'Vitale', (SELECT id FROM team WHERE name = 'Marnera Calcio'), 12, 'P', 8, FALSE),
    ('Antonio', 'Leone', (SELECT id FROM team WHERE name = 'Marnera Calcio'), 22, 'P', 5, FALSE),
    ('Roberto', 'Pellegrini', (SELECT id FROM team WHERE name = 'Marnera Calcio'), 2, 'D', 12, FALSE),
    ('Daniele', 'Martini', (SELECT id FROM team WHERE name = 'Marnera Calcio'), 3, 'D', 10, FALSE),
    ('Giacomo', 'Santoro', (SELECT id FROM team WHERE name = 'Marnera Calcio'), 4, 'D', 9, FALSE),
    ('Michele', 'Caruso', (SELECT id FROM team WHERE name = 'Marnera Calcio'), 5, 'D', 8, FALSE),
    ('Pietro', 'Mancini', (SELECT id FROM team WHERE name = 'Marnera Calcio'), 6, 'D', 7, FALSE),
    ('Vincenzo', 'Rizzo', (SELECT id FROM team WHERE name = 'Marnera Calcio'), 13, 'D', 6, TRUE),
    ('Filippo', 'Lombardi', (SELECT id FROM team WHERE name = 'Marnera Calcio'), 8, 'C', 20, FALSE),
    ('Giovanni', 'Giordano', (SELECT id FROM team WHERE name = 'Marnera Calcio'), 10, 'C', 28, FALSE),
    ('Enrico', 'Basile', (SELECT id FROM team WHERE name = 'Marnera Calcio'), 16, 'C', 16, FALSE),
    ('Salvatore', 'Testa', (SELECT id FROM team WHERE name = 'Marnera Calcio'), 17, 'C', 13, FALSE),
    ('Christian', 'Orlando', (SELECT id FROM team WHERE name = 'Marnera Calcio'), 20, 'C', 11, FALSE),
    ('Ivan', 'Sartori', (SELECT id FROM team WHERE name = 'Marnera Calcio'), 21, 'C', 9, FALSE),
    ('Mattia', 'Pagano', (SELECT id FROM team WHERE name = 'Marnera Calcio'), 9, 'A', 36, FALSE),
    ('Leonardo', 'Rossetti', (SELECT id FROM team WHERE name = 'Marnera Calcio'), 11, 'A', 30, FALSE),
    ('Samuele', 'Fabbri', (SELECT id FROM team WHERE name = 'Marnera Calcio'), 19, 'A', 18, FALSE);

-- Rocca United (strength 58) — 3 P / 6 D / 6 C / 3 A
INSERT INTO player (first_name, last_name, team_id, shirt_number, "position", price, injured) VALUES
    ('Alessandro', 'Bianchi', (SELECT id FROM team WHERE name = 'Rocca United'), 1, 'P', 12, FALSE),
    ('Giuseppe', 'Romano', (SELECT id FROM team WHERE name = 'Rocca United'), 12, 'P', 7, FALSE),
    ('Francesco', 'Galli', (SELECT id FROM team WHERE name = 'Rocca United'), 22, 'P', 5, FALSE),
    ('Domenico', 'Ferrara', (SELECT id FROM team WHERE name = 'Rocca United'), 2, 'D', 10, FALSE),
    ('Salvatore', 'Costantini', (SELECT id FROM team WHERE name = 'Rocca United'), 3, 'D', 9, FALSE),
    ('Angelo', 'Marchetti', (SELECT id FROM team WHERE name = 'Rocca United'), 4, 'D', 8, FALSE),
    ('Raffaele', 'Silvestri', (SELECT id FROM team WHERE name = 'Rocca United'), 5, 'D', 7, FALSE),
    ('Aldo', 'Benedetti', (SELECT id FROM team WHERE name = 'Rocca United'), 6, 'D', 6, FALSE),
    ('Carlo', 'Bianco', (SELECT id FROM team WHERE name = 'Rocca United'), 13, 'D', 5, FALSE),
    ('Emiliano', 'Palumbo', (SELECT id FROM team WHERE name = 'Rocca United'), 8, 'C', 16, FALSE),
    ('Massimo', 'Gentile', (SELECT id FROM team WHERE name = 'Rocca United'), 10, 'C', 22, FALSE),
    ('Claudio', 'Cattaneo', (SELECT id FROM team WHERE name = 'Rocca United'), 16, 'C', 14, TRUE),
    ('Luigi', 'Grasso', (SELECT id FROM team WHERE name = 'Rocca United'), 17, 'C', 11, FALSE),
    ('Renato', 'Damico', (SELECT id FROM team WHERE name = 'Rocca United'), 20, 'C', 9, FALSE),
    ('Ettore', 'Guerra', (SELECT id FROM team WHERE name = 'Rocca United'), 21, 'C', 8, FALSE),
    ('Bruno', 'Parisi', (SELECT id FROM team WHERE name = 'Rocca United'), 9, 'A', 28, FALSE),
    ('Dario', 'Sanna', (SELECT id FROM team WHERE name = 'Rocca United'), 11, 'A', 24, FALSE),
    ('Ivano', 'Monti', (SELECT id FROM team WHERE name = 'Rocca United'), 19, 'A', 14, FALSE);

-- Fenice Sporting (strength 50) — 3 P / 6 D / 6 C / 3 A
INSERT INTO player (first_name, last_name, team_id, shirt_number, "position", price, injured) VALUES
    ('Walter', 'Coppola', (SELECT id FROM team WHERE name = 'Fenice Sporting'), 1, 'P', 10, FALSE),
    ('Ermanno', 'Ferri', (SELECT id FROM team WHERE name = 'Fenice Sporting'), 12, 'P', 6, FALSE),
    ('Sergio', 'Milani', (SELECT id FROM team WHERE name = 'Fenice Sporting'), 22, 'P', 4, FALSE),
    ('Mauro', 'Riva', (SELECT id FROM team WHERE name = 'Fenice Sporting'), 2, 'D', 8, FALSE),
    ('Ivo', 'Negri', (SELECT id FROM team WHERE name = 'Fenice Sporting'), 3, 'D', 7, FALSE),
    ('Corrado', 'Palmieri', (SELECT id FROM team WHERE name = 'Fenice Sporting'), 4, 'D', 6, FALSE),
    ('Gino', 'Sabatini', (SELECT id FROM team WHERE name = 'Fenice Sporting'), 5, 'D', 6, TRUE),
    ('Nino', 'Amato', (SELECT id FROM team WHERE name = 'Fenice Sporting'), 6, 'D', 5, FALSE),
    ('Piero', 'Fiore', (SELECT id FROM team WHERE name = 'Fenice Sporting'), 13, 'D', 5, FALSE),
    ('Osvaldo', 'Battaglia', (SELECT id FROM team WHERE name = 'Fenice Sporting'), 8, 'C', 13, FALSE),
    ('Adriano', 'Serra', (SELECT id FROM team WHERE name = 'Fenice Sporting'), 10, 'C', 19, FALSE),
    ('Fausto', 'Milano', (SELECT id FROM team WHERE name = 'Fenice Sporting'), 16, 'C', 11, FALSE),
    ('Marino', 'Donati', (SELECT id FROM team WHERE name = 'Fenice Sporting'), 17, 'C', 9, FALSE),
    ('Elio', 'Vitali', (SELECT id FROM team WHERE name = 'Fenice Sporting'), 20, 'C', 8, FALSE),
    ('Ugo', 'Cocco', (SELECT id FROM team WHERE name = 'Fenice Sporting'), 21, 'C', 7, FALSE),
    ('Gaetano', 'Battisti', (SELECT id FROM team WHERE name = 'Fenice Sporting'), 9, 'A', 22, FALSE),
    ('Franco', 'De Luca', (SELECT id FROM team WHERE name = 'Fenice Sporting'), 11, 'A', 18, FALSE),
    ('Settimio', 'Piras', (SELECT id FROM team WHERE name = 'Fenice Sporting'), 19, 'A', 10, FALSE);
