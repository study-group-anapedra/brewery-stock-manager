CREATE OR REPLACE FUNCTION find_beers_using_filters(
    p_beer_id               BIGINT DEFAULT NULL,
    p_beer_description      TEXT DEFAULT NULL,
    p_min_quantity          INTEGER DEFAULT NULL,
    p_max_quantity          INTEGER DEFAULT NULL,
    p_days_until_expiry     INTEGER DEFAULT NULL,
    p_page_size             INTEGER DEFAULT 10,
    p_page_number           INTEGER DEFAULT 0
)
RETURNS TABLE (
    id              BIGINT,
    name            VARCHAR,
    price           NUMERIC,
    expiration_date DATE
)
LANGUAGE plpgsql
AS $$
DECLARE
v_offset INTEGER := 0;
BEGIN
    -- Cálculo de página
    IF p_page_size > 0 THEN
        v_offset := p_page_number * p_page_size;
END IF;

RETURN QUERY
SELECT DISTINCT
    b.id,
    b.name,
    b.price,
    b.expiration_date
FROM tb_beers b
         LEFT JOIN tb_stock s ON s.beer_id = b.id
WHERE
  -- Filtro por ID
    (p_beer_id IS NULL OR b.id = p_beer_id)

  -- Filtro por Descrição (Trata vazio como NULL para não ignorar outros filtros)
  AND (NULLIF(TRIM(p_beer_description), '') IS NULL
    OR LOWER(b.name) LIKE LOWER(CONCAT('%', TRIM(p_beer_description), '%')))

  -- Filtro por Quantidade
  AND (p_min_quantity IS NULL OR COALESCE(s.quantity, 0) >= p_min_quantity)
  AND (p_max_quantity IS NULL OR COALESCE(s.quantity, 0) <= p_max_quantity)

  -- Filtro por Prazo de Validade
  AND (p_days_until_expiry IS NULL
    OR (b.expiration_date > CURRENT_DATE
        AND (b.expiration_date - CURRENT_DATE) <= p_days_until_expiry))
ORDER BY b.id
    LIMIT p_page_size
OFFSET v_offset;
END;
$$;