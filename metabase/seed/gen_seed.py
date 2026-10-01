import random
from datetime import datetime, timedelta, date

random.seed(42)
NOW = datetime(2026, 9, 29, 15, 0, 0)
MONTHS = [(2025, 10), (2025, 11), (2025, 12), (2026, 1), (2026, 2), (2026, 3),
          (2026, 4), (2026, 5), (2026, 6), (2026, 7), (2026, 8), (2026, 9)]

TABLE_CODE = {"users": 1, "profile": 2, "animal": 3, "location": 4, "animal_post": 5, "history": 6,
              "campaign": 7, "item": 8, "vet": 9, "schedule": 10}
counters = {}
out = []


def uid(kind):
    counters[kind] = counters.get(kind, 0) + 1
    return "UNHEX('5EED%04X%024X')" % (TABLE_CODE[kind], counters[kind])


def q(v):
    if v is None:
        return "NULL"
    if isinstance(v, bool):
        return "1" if v else "0"
    if isinstance(v, (int, float)):
        return str(v)
    if isinstance(v, datetime):
        return "'%s'" % v.strftime("%Y-%m-%d %H:%M:%S.%f")
    if isinstance(v, date):
        return "'%s'" % v.isoformat()
    return "'%s'" % str(v).replace("\\", "\\\\").replace("'", "''")


rows = {}


def insert(table, cols, values):
    rows.setdefault((table, tuple(cols)), []).append("(" + ", ".join(v if isinstance(v, str) and v.startswith("UNHEX(") else q(v) for v in values) + ")")


def flush():
    for (table, cols), vals in rows.items():
        for i in range(0, len(vals), 100):
            out.append("INSERT INTO %s (%s) VALUES\n  %s;" % (table, ", ".join(cols), ",\n  ".join(vals[i:i + 100])))
    rows.clear()


def random_moment(y, m, hard_limit=NOW):
    start = datetime(y, m, 1)
    end = datetime(y + (m == 12), (m % 12) + 1, 1) - timedelta(seconds=1)
    end = min(end, hard_limit - timedelta(days=1))
    span = int((end - start).total_seconds())
    return start + timedelta(seconds=random.randint(0, max(span, 1)), microseconds=random.randint(0, 999999))


PLACES = [
    ("Av. Colon", "Nueva Cordoba", -31.4201, -64.1888), ("Bv. San Juan", "Centro", -31.4234, -64.1911),
    ("Av. Velez Sarsfield", "Alberdi", -31.4130, -64.1930), ("Av. Rafael Nunez", "Cerro de las Rosas", -31.3711, -64.2350),
    ("Av. Cabildo", "Belgrano", -34.5627, -58.4560), ("Av. Corrientes", "Almagro", -34.6037, -58.4171),
    ("Av. Rivadavia", "Caballito", -34.6187, -58.4437), ("Av. Santa Fe", "Palermo", -34.5889, -58.4104),
    ("Calle San Martin", "Centro", -32.9468, -60.6393), ("Av. Pellegrini", "Rosario Centro", -32.9587, -60.6670),
    ("Calle Belgrano", "Godoy Cruz", -32.9270, -68.8340), ("Av. San Martin", "Ciudad", -32.8908, -68.8272),
]
DOG_NAMES = ["Firulais", "Luna", "Thor", "Mora", "Rocky", "Canela", "Simba", "Nina", "Toby", "Bonnie", "Chester", "Lola", "Max", "Kira", "Olivia", "Bruno"]
CAT_NAMES = ["Mishi", "Garfield", "Pelusa", "Michi", "Tigre", "Nala", "Salem", "Coco", "Felix", "Morocha"]
COLORS = ["Negro", "Blanco", "Marron", "Gris", "Naranja", "Tricolor", "Manchado", "Beige"]


def make_location(created_at):
    a, barrio, lat, lon = random.choice(PLACES)
    lid = uid("location")
    insert("location", ["id", "address", "latitude", "longitude", "name", "number"],
           [lid, a, round(lat + random.uniform(-0.01, 0.01), 6), round(lon + random.uniform(-0.01, 0.01), 6), barrio, random.randint(100, 4500)])
    return lid


def make_animal():
    aid = uid("animal")
    kind = random.choices(["DOG", "CAT", "OTHER"], [55, 38, 7])[0]
    insert("animal", ["id", "age", "color", "gender", "size", "type"],
           [aid, random.choice(["ADULT", "PUPPY", "SENIOR", "UNKNOWN"]), random.choice(COLORS),
            random.choice(["FEMALE", "MALE", "UNKNOWN"]),
            random.choice(["SMALL", "MEDIUM", "LARGE"] if kind == "DOG" else ["SMALL", "MEDIUM"]), kind])
    names = CAT_NAMES if kind == "CAT" else DOG_NAMES
    return aid, kind, random.choice(names)


def history(table, fk, parent, chain, start, moments=None):
    """Inserta la cadena de estados sin pasar de NOW. Devuelve (ultimo_estado, momento_del_ultimo)."""
    if moments is None:
        moments, cur = [], start
        for _ in chain:
            moments.append(cur)
            cur = cur + timedelta(days=random.randint(1, 20), hours=random.randint(0, 23), minutes=random.randint(0, 59))
    kept = [(st, mo) for st, mo in zip(chain, moments) if mo <= NOW]
    for i, (status, moment) in enumerate(kept):
        is_last = i == len(kept) - 1
        insert(table, ["id", "created_at", "finished_at", "status", fk],
               [uid("history"), moment, None if is_last else kept[i + 1][1], status, parent])
    return kept[-1]


def cap(chain, moment_of_creation):
    return chain


# ---------- USERS ----------
USERS = []
first_names = ["Ana", "Bruno", "Carla", "Diego", "Elena", "Fabian", "Gabriela", "Hector", "Ines", "Javier", "Karina", "Lucas", "Marta", "Nicolas", "Olga", "Pablo"]
last_names = ["Gomez", "Perez", "Rodriguez", "Fernandez", "Lopez", "Martinez", "Sanchez", "Diaz", "Romero", "Torres"]
n_user = 0
for idx, (y, m) in enumerate(MONTHS):
    for _ in range(4 + idx):
        n_user += 1
        created = random_moment(y, m)
        pid, usr = uid("profile"), uid("users")
        fn, ln = random.choice(first_names), random.choice(last_names)
        insert("profile", ["id", "email", "lastname", "name", "area_code", "phone_number"],
               [pid, "seed.user%d@seed.local" % n_user, ln, fn, random.choice(["351", "11", "341", "261"]), str(random.randint(4000000, 6999999))])
        insert("users", ["id", "created_at", "password", "username", "profile_id"],
               [usr, created, "SEED-NO-LOGIN", "seed_user_%d" % n_user, pid])
        roles = random.choices(["COMMUNITY", "RESCUER", "TRANSITIONAL_HOME", "CARRIAGE", "VET"], [50, 18, 14, 12, 6])
        for r in set(roles + ["COMMUNITY"] if random.random() < .5 else roles):
            insert("profile_roles", ["profile_id", "role"], [pid, r])
        USERS.append((usr, created))

# ---------- LOST / IN STREET POSTS ----------
for idx, (y, m) in enumerate(MONTHS):
    for _ in range(int(8 + idx * 1.3 + random.randint(-2, 3))):
        created = random_moment(y, m)
        aid, kind, name = make_animal()
        pid = uid("animal_post")
        has_owner = random.random() < 0.45
        owner = random.choice([u for u, c in USERS if c < created] or [USERS[0][0]])
        insert("animal_post", ["id", "created_at", "description", "name", "area_code", "phone_number", "updated_at", "animal_id", "location_id", "owner_id"],
               [pid, created, "Visto por ultima vez en la zona." if has_owner else "Animal en situacion de calle, necesita ayuda.",
                name if has_owner else None, "351" if random.random() < .8 else None,
                str(random.randint(4000000, 6999999)) if random.random() < .8 else None,
                created, aid, make_location(created), owner])
        insert("lost_post", ["id", "has_owner", "reward"], [pid, has_owner, random.choice([None, None, 15000, 30000, 50000]) if has_owner else None])
        age_months = len(MONTHS) - 1 - idx
        resolved = random.random() < min(0.85, 0.30 + 0.07 * age_months)
        if has_owner:
            chain = ["CREATED", "SEARCHING"] + (["FOUND"] if resolved else [])
        else:
            chain = ["CREATED", "TO_RESCUE"] + (["RESCUED"] if resolved else [])
        if random.random() < 0.08:
            chain = ["CREATED"]
        history("lost_post_status_history", "lost_post_id", pid, chain, created)

# ---------- ADOPTION POSTS ----------
for idx, (y, m) in enumerate(MONTHS):
    for _ in range(int(3 + idx * 0.6 + random.randint(-1, 2))):
        created = random_moment(y, m)
        aid, kind, name = make_animal()
        pid = uid("animal_post")
        owner = random.choice([u for u, c in USERS if c < created] or [USERS[0][0]])
        insert("animal_post", ["id", "created_at", "description", "name", "area_code", "phone_number", "updated_at", "animal_id", "location_id", "owner_id"],
               [pid, created, "Busca familia responsable. Vacunado y desparasitado.", name, "351",
                str(random.randint(4000000, 6999999)), created, aid, make_location(created), owner])
        insert("adoption_post", ["id"], [pid])
        age_months = len(MONTHS) - 1 - idx
        adopted = random.random() < min(0.8, 0.25 + 0.07 * age_months)
        chain = ["CREATED", random.choice(["SEARCHING_ADOPT", "SEARCHING_ADOPT", "SEARCHING_ADOPT_AND_TRANSIT"])] + (["ADOPTED"] if adopted else [])
        history("adoption_post_status_history", "adoption_post_id", pid, chain, created)

# ---------- CAMPAIGNS ----------
CAMPAIGN_COLS = ["id", "created_at", "description", "finished_at", "area_code", "phone_number", "title", "updated_at", "location_id", "owner_id"]
for idx, (y, m) in enumerate(MONTHS):
    age_months = len(MONTHS) - 1 - idx
    for _ in range(random.randint(1, 3)):
        # FUNDRAISING
        created = random_moment(y, m)
        cid = uid("campaign")
        goal = random.choice([80000, 150000, 250000, 400000])
        completed = random.random() < min(0.75, 0.25 + 0.08 * age_months)
        finished = (not completed) and age_months >= 2 and random.random() < 0.5
        collected = goal if completed else int(goal * random.uniform(0.05, 0.85))
        end_dt = (created + timedelta(days=random.randint(30, 75)))
        chain = ["CREATED"] + (["COMPLETED"] if completed else ["FINISHED"] if finished else [])
        last_status, last = history("fundraising_campaign_status_history", "fundraising_campaign_id", cid, chain, created)
        insert("fundraising_campaign",
               CAMPAIGN_COLS + ["account_alias", "amount_collected", "amount_to_be_collected", "campaign_end_date"],
               [cid, created, "Ayudanos a sostener el refugio.", last if last_status != "CREATED" else None, "351", str(random.randint(4000000, 6999999)),
                random.choice(["Cirugia para Toby", "Alimento para el refugio", "Tratamiento de Luna", "Refugio de invierno"]), created,
                make_location(created), random.choice(USERS)[0], "manada.solidaria.%d" % random.randint(1, 99), collected, goal, end_dt.date()])

    for _ in range(random.randint(1, 2)):
        # DONATION
        created = random_moment(y, m)
        cid = uid("campaign")
        completed = random.random() < min(0.7, 0.2 + 0.08 * age_months)
        finished = (not completed) and age_months >= 2 and random.random() < 0.5
        chain = ["CREATED"] + (["COMPLETED"] if completed else ["FINISHED"] if finished else [])
        last_status, last = history("donation_campaign_status_history", "donation_campaign_id", cid, chain, created)
        insert("donation_campaign", CAMPAIGN_COLS + ["campaign_end_date"],
               [cid, created, "Juntamos donaciones para los animales del refugio.", last if last_status != "CREATED" else None, "351",
                str(random.randint(4000000, 6999999)), random.choice(["Colecta de alimento", "Mantas para el invierno", "Botiquin solidario"]),
                created, make_location(created), random.choice(USERS)[0], (created + timedelta(days=random.randint(20, 60))).date()])
        for cat, item in random.sample([("FOOD", "Alimento balanceado"), ("CLOTHING_AND_BLANKETS", "Mantas"), ("MEDICINE", "Antiparasitarios"),
                                        ("TOYS_AND_ACCESSORIES", "Correas"), ("SHELTER_AND_BEDDING", "Camas"), ("OTHER", "Bandejas")], random.randint(2, 4)):
            insert("donation_item", ["id", "category", "is_completed", "name", "donation_campaign_id"],
                   [uid("item"), cat, completed or random.random() < 0.3, item, cid])

    for _ in range(random.randint(1, 3)):
        # NEWS (jornadas)
        created = random_moment(y, m)
        cid = uid("campaign")
        start = created + timedelta(days=random.randint(3, 20), hours=2)
        end = start + timedelta(hours=random.randint(3, 8))
        chain = ["CREATED"]
        if start <= NOW:
            chain.append("STARTED")
        if end <= NOW:
            chain.append("FINISHED")
        history("news_campaign_status_history", "news_campaign_id", cid, chain, created, [created, start, end][:len(chain)])
        insert("news_campaign", CAMPAIGN_COLS + ["category", "news_end_date_time", "news_start_date_time"],
               [cid, created, "Jornada abierta a la comunidad.", end if chain[-1] == "FINISHED" else None, "351",
                str(random.randint(4000000, 6999999)), random.choice(["Jornada de castracion", "Vacunacion antirrabica", "Desparasitacion gratuita"]),
                created, make_location(created), random.choice(USERS)[0], random.choice(["CASTRATION", "VACCINATION", "DEWORMING", "OTHER"]),
                end, start])

# ---------- VETS + SCHEDULES ----------
VET_NAMES = ["Veterinaria Patitas", "Clinica Huellitas", "Veterinaria San Roque", "Pet Center Norte", "Veterinaria del Parque",
             "Clinica Animal Sur", "Veterinaria La Mascota", "Centro Veterinario Belgrano", "Veterinaria Cuatro Patas",
             "Clinica Veterinaria Alberdi", "Veterinaria Vida Animal", "Hospital Veterinario Centro"]
DAYS = ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"]
for i, vname in enumerate(VET_NAMES):
    vid = uid("vet")
    insert("vet_information", ["id", "description", "email", "name", "area_code", "phone_number", "location_id"],
           [vid, "Atencion clinica, vacunacion y cirugias.", "contacto%d@seed.local" % (i + 1), vname,
            random.choice(["351", "11", "341", "261"]), str(random.randint(4000000, 6999999)), make_location(NOW)])
    late = random.random() < 0.3
    close = "22:00:00" if late else random.choice(["19:00:00", "20:00:00", "18:00:00"])
    for d in DAYS:
        insert("schedule", ["id", "closing_time", "day_of_week", "opening_time", "vet_id"], [uid("schedule"), close, d, "09:00:00", vid])
    if random.random() < 0.55:
        insert("schedule", ["id", "closing_time", "day_of_week", "opening_time", "vet_id"], [uid("schedule"), "13:00:00", "SATURDAY", "09:00:00", vid])
    if random.random() < 0.2:
        insert("schedule", ["id", "closing_time", "day_of_week", "opening_time", "vet_id"], [uid("schedule"), "12:00:00", "SUNDAY", "10:00:00", vid])

flush()

CLEAN = """-- Seed de metricas: datos de prueba para Manada Solidaria (SOLO local / desarrollo).
-- Todo lo sembrado lleva el prefijo 5EED en el id, asi se puede borrar sin tocar datos reales.
-- Uso: mysql -uroot -p manada_solidaria < seed-metricas.sql   (es idempotente: borra y vuelve a cargar)
SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM lost_post_status_history        WHERE HEX(id) LIKE '5EED%';
DELETE FROM adoption_post_status_history    WHERE HEX(id) LIKE '5EED%';
DELETE FROM fundraising_campaign_status_history WHERE HEX(id) LIKE '5EED%';
DELETE FROM donation_campaign_status_history    WHERE HEX(id) LIKE '5EED%';
DELETE FROM news_campaign_status_history        WHERE HEX(id) LIKE '5EED%';
DELETE FROM donation_item        WHERE HEX(id) LIKE '5EED%';
DELETE FROM lost_post            WHERE HEX(id) LIKE '5EED%';
DELETE FROM adoption_post        WHERE HEX(id) LIKE '5EED%';
DELETE FROM animal_post          WHERE HEX(id) LIKE '5EED%';
DELETE FROM animal               WHERE HEX(id) LIKE '5EED%';
DELETE FROM fundraising_campaign WHERE HEX(id) LIKE '5EED%';
DELETE FROM donation_campaign    WHERE HEX(id) LIKE '5EED%';
DELETE FROM news_campaign        WHERE HEX(id) LIKE '5EED%';
DELETE FROM schedule             WHERE HEX(id) LIKE '5EED%';
DELETE FROM vet_information      WHERE HEX(id) LIKE '5EED%';
DELETE FROM location             WHERE HEX(id) LIKE '5EED%';
DELETE FROM profile_roles        WHERE HEX(profile_id) LIKE '5EED%';
DELETE FROM users                WHERE HEX(id) LIKE '5EED%';
DELETE FROM profile              WHERE HEX(id) LIKE '5EED%';
SET FOREIGN_KEY_CHECKS = 1;
"""
with open("seed-metricas.sql", "w", encoding="utf-8", newline="\n") as f:
    f.write(CLEAN + "\nSET FOREIGN_KEY_CHECKS = 0;\n" + "\n".join(out) + "\nSET FOREIGN_KEY_CHECKS = 1;\n")
print({k: v for k, v in counters.items()})
