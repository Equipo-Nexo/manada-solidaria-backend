import json, os, urllib.request

MB_URL = os.environ.get("MB_URL", "http://localhost:3000")
H = {"X-Metabase-Session": os.environ["MB_SESSION"], "Content-Type": "application/json"}


def api(method, path, body=None):
    req = urllib.request.Request(MB_URL + "/api" + path, method=method, headers=H,
                                 data=json.dumps(body).encode() if body is not None else None)
    with urllib.request.urlopen(req) as r:
        raw = r.read()
        return json.loads(raw) if raw else None


DB_ID = int(os.environ.get("DB_ID", 2))
meta = api("GET", f"/database/{DB_ID}/metadata?include_hidden=true")
fid = {(t["name"], f["name"]): f["id"] for t in meta["tables"] for f in t["fields"]}

FECHA_TAG = lambda table: {"fecha": {"id": "f-fecha", "name": "fecha", "display-name": "Fecha", "type": "dimension",
                                     "dimension": ["field", fid[(table, "created_at")], None], "widget-type": "date/all-options"}}


def card(name, sql, display, filter_table=None, viz=None, titles=None, description=None):
    settings = dict(viz or {})
    if titles:
        settings["column_settings"] = {json.dumps(["name", k]): {"column_title": v} for k, v in titles.items()}
    body = {"name": name, "display": display, "visualization_settings": settings, "description": description,
            "dataset_query": {"database": DB_ID, "type": "native",
                              "native": {"query": sql, "template-tags": FECHA_TAG(filter_table) if filter_table else {}}}}
    c = api("POST", "/card", body)
    c["_filtered"] = bool(filter_table)
    return c


CURRENT = "h.finished_at IS NULL"
DESC_PERIODO = "Sobre los casos reportados en el periodo elegido en el filtro de fecha."

cards = {}

cards["rescatados"] = card("Rescatados", f"""SELECT COUNT(*) AS rescatados
FROM animal_post
JOIN lost_post l ON l.id = animal_post.id AND l.has_owner = 0
JOIN lost_post_status_history h ON h.lost_post_id = l.id AND {CURRENT} AND h.status = 'RESCUED'
WHERE 1=1 [[ AND {{{{fecha}}}} ]]""", "scalar", "animal_post", description="Animales rescatados. " + DESC_PERIODO)
cards["reencontrados"] = card("Encontrados", f"""SELECT COUNT(*) AS reencontradas
FROM animal_post
JOIN lost_post l ON l.id = animal_post.id AND l.has_owner = 1
JOIN lost_post_status_history h ON h.lost_post_id = l.id AND {CURRENT} AND h.status = 'FOUND'
WHERE 1=1 [[ AND {{{{fecha}}}} ]]""", "scalar", "animal_post", description="Mascotas con dueño reencontradas. " + DESC_PERIODO)
cards["adoptados"] = card("Adoptados", f"""SELECT COUNT(*) AS adoptados
FROM animal_post
JOIN adoption_post a ON a.id = animal_post.id
JOIN adoption_post_status_history h ON h.adoption_post_id = a.id AND {CURRENT} AND h.status = 'ADOPTED'
WHERE 1=1 [[ AND {{{{fecha}}}} ]]""", "scalar", "animal_post", description="Adopciones concretadas. " + DESC_PERIODO)
cards["usuarios_total"] = card("Usuarios", "SELECT COUNT(*) AS usuarios FROM users", "scalar",
                               description="Usuarios registrados desde el inicio. No depende del filtro de fecha.")
cards["vets_total"] = card("Veterinarias", "SELECT COUNT(*) AS veterinarias FROM vet_information", "scalar",
                           description="Veterinarias asociadas a Manada Solidaria hoy. No depende del filtro de fecha.")
cards["campanias_total"] = card("Campañas", """SELECT
  (SELECT COUNT(*) FROM fundraising_campaign) + (SELECT COUNT(*) FROM donation_campaign) + (SELECT COUNT(*) FROM news_campaign) AS campanias""",
                                "scalar", description="Campañas creadas desde el inicio: recaudaciones, donaciones y jornadas. No depende del filtro de fecha.")

resultado_sql = f"""SELECT 'En la calle' AS caso, COUNT(*) AS reportados, SUM(h.status = 'RESCUED') AS resueltos,
       ROUND(100 * SUM(h.status = 'RESCUED') / COUNT(*), 1) AS tasa
FROM animal_post
JOIN lost_post l ON l.id = animal_post.id AND l.has_owner = 0
JOIN lost_post_status_history h ON h.lost_post_id = l.id AND {CURRENT}
WHERE 1=1 [[ AND {{{{fecha}}}} ]]
UNION ALL
SELECT 'Perdidos con dueño', COUNT(*), SUM(h.status = 'FOUND'), ROUND(100 * SUM(h.status = 'FOUND') / COUNT(*), 1)
FROM animal_post
JOIN lost_post l ON l.id = animal_post.id AND l.has_owner = 1
JOIN lost_post_status_history h ON h.lost_post_id = l.id AND {CURRENT}
WHERE 1=1 [[ AND {{{{fecha}}}} ]]
UNION ALL
SELECT 'En adopción', COUNT(*), SUM(h.status = 'ADOPTED'), ROUND(100 * SUM(h.status = 'ADOPTED') / COUNT(*), 1)
FROM animal_post
JOIN adoption_post a ON a.id = animal_post.id
JOIN adoption_post_status_history h ON h.adoption_post_id = a.id AND {CURRENT}
WHERE 1=1 [[ AND {{{{fecha}}}} ]]"""
cards["resultado"] = card("Resultado de los casos", resultado_sql, "table", "animal_post",
                          titles={"caso": "Tipo de caso", "reportados": "Reportados", "resueltos": "Resueltos", "tasa": "Resueltos (%)"},
                          description=DESC_PERIODO + " Resuelto = rescatado, encontrado o adoptado a la fecha de hoy.")

tiempo_sql = f"""SELECT 'En la calle (rescatados)' AS caso, COUNT(*) AS resueltos,
       ROUND(AVG(TIMESTAMPDIFF(SECOND, animal_post.created_at, h.created_at)) / 86400, 1) AS dias
FROM animal_post
JOIN lost_post l ON l.id = animal_post.id AND l.has_owner = 0
JOIN lost_post_status_history h ON h.lost_post_id = l.id AND h.status = 'RESCUED'
WHERE 1=1 [[ AND {{{{fecha}}}} ]]
UNION ALL
SELECT 'Perdidos con dueño (encontrados)', COUNT(*), ROUND(AVG(TIMESTAMPDIFF(SECOND, animal_post.created_at, h.created_at)) / 86400, 1)
FROM animal_post
JOIN lost_post l ON l.id = animal_post.id AND l.has_owner = 1
JOIN lost_post_status_history h ON h.lost_post_id = l.id AND h.status = 'FOUND'
WHERE 1=1 [[ AND {{{{fecha}}}} ]]
UNION ALL
SELECT 'En adopción (adoptados)', COUNT(*), ROUND(AVG(TIMESTAMPDIFF(SECOND, animal_post.created_at, h.created_at)) / 86400, 1)
FROM animal_post
JOIN adoption_post a ON a.id = animal_post.id
JOIN adoption_post_status_history h ON h.adoption_post_id = a.id AND h.status = 'ADOPTED'
WHERE 1=1 [[ AND {{{{fecha}}}} ]]"""
cards["tiempo"] = card("Tiempo medio hasta resolver un caso", tiempo_sql, "table", "animal_post",
                       titles={"caso": "Tipo de caso", "resueltos": "Casos resueltos", "dias": "Días promedio"},
                       description=DESC_PERIODO + " Días desde que se publicó hasta que se resolvió.")

cards["por_mes"] = card("Casos reportados por mes", """SELECT DATE_FORMAT(animal_post.created_at, '%Y-%m') AS mes,
       CASE WHEN a.id IS NOT NULL THEN 'En adopción' WHEN l.has_owner = 1 THEN 'Perdidos con dueño' ELSE 'En la calle' END AS tipo,
       COUNT(*) AS casos
FROM animal_post
LEFT JOIN lost_post l ON l.id = animal_post.id
LEFT JOIN adoption_post a ON a.id = animal_post.id
WHERE 1=1 [[ AND {{fecha}} ]]
GROUP BY mes, tipo
ORDER BY mes""", "bar", "animal_post",
                        viz={"graph.dimensions": ["mes", "tipo"], "graph.metrics": ["casos"], "stackable.stack_type": "stacked"})

cards["abiertos"] = card("Casos abiertos hoy", """SELECT etiqueta AS estado, COUNT(*) AS casos FROM (
  SELECT CASE WHEN l.has_owner = 1
              THEN CASE h.status WHEN 'CREATED' THEN 'Perdidos: sin gestionar' ELSE 'Perdidos: en búsqueda' END
              ELSE CASE h.status WHEN 'CREATED' THEN 'En la calle: sin gestionar' ELSE 'En la calle: a rescatar' END END AS etiqueta
  FROM lost_post l
  JOIN lost_post_status_history h ON h.lost_post_id = l.id AND h.finished_at IS NULL AND h.status IN ('CREATED', 'SEARCHING', 'TO_RESCUE')
  UNION ALL
  SELECT CASE h.status WHEN 'CREATED' THEN 'Adopción: sin gestionar' ELSE 'Adopción: buscando familia' END
  FROM adoption_post_status_history h
  WHERE h.finished_at IS NULL AND h.status <> 'ADOPTED'
) abiertos
GROUP BY etiqueta
ORDER BY casos DESC""", "row", description="Casos que hoy siguen sin resolverse. No depende del filtro de fecha.")

cards["animal"] = card("Tipo de animal", """SELECT CASE an.type WHEN 'DOG' THEN 'Perros' WHEN 'CAT' THEN 'Gatos' ELSE 'Otros' END AS tipo, COUNT(*) AS casos
FROM animal_post
JOIN animal an ON an.id = animal_post.animal_id
WHERE 1=1 [[ AND {{fecha}} ]]
GROUP BY tipo""", "pie", "animal_post", viz={"pie.dimension": "tipo", "pie.metric": "casos"}, description=DESC_PERIODO)

cards["campanias_mes"] = card("Campañas creadas por mes", """SELECT mes, tipo, COUNT(*) AS campanias FROM (
  SELECT DATE_FORMAT(created_at, '%Y-%m') AS mes, 'Recaudación' AS tipo FROM fundraising_campaign
  UNION ALL SELECT DATE_FORMAT(created_at, '%Y-%m'), 'Donaciones' FROM donation_campaign
  UNION ALL SELECT DATE_FORMAT(created_at, '%Y-%m'), 'Jornadas' FROM news_campaign
) c GROUP BY mes, tipo ORDER BY mes""", "bar",
                              viz={"graph.dimensions": ["mes", "tipo"], "graph.metrics": ["campanias"], "stackable.stack_type": "stacked"},
                              description="Historial completo. No depende del filtro de fecha.")

cards["recaudacion"] = card("Campañas de recaudación", f"""SELECT COUNT(*) AS cantidad, SUM(h.status = 'COMPLETED') AS completadas,
       SUM(amount_to_be_collected) AS meta, SUM(amount_collected) AS recaudado,
       ROUND(100 * SUM(amount_collected) / SUM(amount_to_be_collected), 1) AS pct
FROM fundraising_campaign
JOIN fundraising_campaign_status_history h ON h.fundraising_campaign_id = fundraising_campaign.id AND {CURRENT}
WHERE 1=1 [[ AND {{{{fecha}}}} ]]""", "table", "fundraising_campaign",
                            titles={"cantidad": "Campañas", "completadas": "Completadas", "meta": "Meta ($)", "recaudado": "Recaudado ($)", "pct": "% de la meta"},
                            description="Campañas creadas en el periodo elegido.")
cards["donaciones"] = card("Campañas de donaciones", f"""SELECT COUNT(*) AS cantidad, SUM(h.status = 'COMPLETED') AS completadas,
       ROUND(100 * SUM(h.status = 'COMPLETED') / COUNT(*), 1) AS pct
FROM donation_campaign
JOIN donation_campaign_status_history h ON h.donation_campaign_id = donation_campaign.id AND {CURRENT}
WHERE 1=1 [[ AND {{{{fecha}}}} ]]""", "table", "donation_campaign",
                           titles={"cantidad": "Campañas", "completadas": "Completadas", "pct": "% completadas"},
                           description="Campañas creadas en el periodo elegido.")
cards["jornadas"] = card("Jornadas comunitarias por categoría", """SELECT CASE category WHEN 'CASTRATION' THEN 'Castración' WHEN 'VACCINATION' THEN 'Vacunación'
       WHEN 'DEWORMING' THEN 'Desparasitación' ELSE 'Otras' END AS categoria, COUNT(*) AS jornadas
FROM news_campaign
WHERE 1=1 [[ AND {{fecha}} ]]
GROUP BY categoria
ORDER BY jornadas DESC""", "bar", "news_campaign", description="Jornadas creadas en el periodo elegido.")

cards["usuarios_mes"] = card("Usuarios nuevos por mes", """SELECT DATE_FORMAT(created_at, '%Y-%m') AS mes, COUNT(*) AS usuarios
FROM users
WHERE 1=1 [[ AND {{fecha}} ]]
GROUP BY mes
ORDER BY mes""", "bar", "users")
cards["roles"] = card("Roles especializados", """SELECT CASE role WHEN 'RESCUER' THEN 'Rescatistas' WHEN 'VET' THEN 'Veterinarios' WHEN 'TRANSITIONAL_HOME' THEN 'Hogares de tránsito'
       WHEN 'CARRIAGE' THEN 'Transportistas' END AS rol, COUNT(DISTINCT profile_id) AS personas
FROM profile_roles
WHERE role <> 'COMMUNITY'
GROUP BY rol
ORDER BY personas DESC""", "row", description="Personas que ofrecen ayuda especializada (todos tienen además el rol base de comunidad). Una persona puede tener más de un rol. No depende del filtro de fecha.")
cards["cobertura"] = card("Cobertura de la red veterinaria", """SELECT 'Veterinarias asociadas' AS cobertura, COUNT(*) AS veterinarias FROM vet_information
UNION ALL SELECT 'Con horarios cargados', COUNT(DISTINCT vet_id) FROM schedule
UNION ALL SELECT 'Atienden los sábados', COUNT(DISTINCT vet_id) FROM schedule WHERE day_of_week = 'SATURDAY'
UNION ALL SELECT 'Atienden los domingos', COUNT(DISTINCT vet_id) FROM schedule WHERE day_of_week = 'SUNDAY'
UNION ALL SELECT 'Cierran a las 20 hs o más tarde', COUNT(DISTINCT vet_id) FROM schedule WHERE closing_time >= '20:00:00'""", "table",
                          titles={"cobertura": "Cobertura", "veterinarias": "Veterinarias"},
                          description="Estado actual de la red. No depende del filtro de fecha.")

def heading(text, row):
    return {"card_id": None, "row": row, "col": 0, "size_x": 24, "size_y": 1,
            "visualization_settings": {"virtual_card": {"name": None, "display": "heading", "visualization_settings": {}, "dataset_query": {}, "archived": False},
                                       "text": text}}


def place(key, row, col, w, h):
    c = cards[key]
    d = {"card_id": c["id"], "row": row, "col": col, "size_x": w, "size_y": h, "visualization_settings": {}}
    if c["_filtered"]:
        d["parameter_mappings"] = [{"parameter_id": "fecha", "card_id": c["id"], "target": ["dimension", ["template-tag", "fecha"]]}]
    return d


layout = [
    heading("Resumen", 0),
    place("rescatados", 1, 0, 4, 3), place("reencontrados", 1, 4, 4, 3), place("adoptados", 1, 8, 4, 3),
    place("usuarios_total", 1, 12, 4, 3), place("vets_total", 1, 16, 4, 3), place("campanias_total", 1, 20, 4, 3),
    heading("Casos: rescate, reencuentro y adopción", 4),
    place("resultado", 5, 0, 12, 5), place("tiempo", 5, 12, 12, 5),
    place("por_mes", 10, 0, 24, 6),
    place("abiertos", 16, 0, 14, 6), place("animal", 16, 14, 10, 6),
    heading("Campañas", 22),
    place("campanias_mes", 23, 0, 24, 6),
    place("recaudacion", 29, 0, 14, 4), place("donaciones", 29, 14, 10, 4),
    place("jornadas", 33, 0, 24, 5),
    heading("Comunidad y red veterinaria", 38),
    place("usuarios_mes", 39, 0, 12, 6), place("roles", 39, 12, 12, 6),
    place("cobertura", 45, 0, 12, 7),
]
for i, d in enumerate(layout):
    d["id"] = -(i + 1)

dash_id = int(os.environ["DASH_ID"]) if "DASH_ID" in os.environ else api("POST", "/dashboard", {"name": "Metricas Manada Solidaria"})["id"]
old = api("GET", f"/dashboard/{dash_id}")
old_cards = [dc["card_id"] for dc in old["dashcards"] if dc.get("card_id")]
api("PUT", f"/dashboard/{dash_id}", {"name": "Métricas Manada Solidaria",
                                      "parameters": [{"id": "fecha", "name": "Fecha", "slug": "fecha", "type": "date/all-options"}],
                                      "dashcards": layout})
for cid in old_cards:
    api("DELETE", f"/card/{cid}")
new = api("GET", f"/dashboard/{dash_id}")
print("dashcards:", len(new["dashcards"]), "| cards viejas borradas:", old_cards)
