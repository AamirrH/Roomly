export function responseItems(payload) {
  if (Array.isArray(payload)) return payload;
  if (Array.isArray(payload?.content)) return payload.content;
  if (Array.isArray(payload?.data)) return payload.data;
  if (Array.isArray(payload?.items)) return payload.items;
  if (Array.isArray(payload?._embedded?.hotels)) return payload._embedded.hotels;
  if (Array.isArray(payload?._embedded?.rooms)) return payload._embedded.rooms;
  return [];
}

export function pageInfo(payload, fallback = {}) {
  const metadata = payload?.page || payload?.pageable || payload || {};
  const items = responseItems(payload);
  const number = Number(payload?.number ?? metadata.number ?? metadata.pageNumber ?? fallback.number ?? 0);
  const size = Number(payload?.size ?? metadata.size ?? metadata.pageSize ?? fallback.size ?? items.length);
  const totalElements = Number(
    payload?.totalElements ??
      metadata.totalElements ??
      payload?.total_elements ??
      metadata.total_elements ??
      fallback.totalElements ??
      items.length
  );
  const calculatedTotalPages = size > 0 ? Math.ceil(totalElements / size) : 1;
  const totalPages = Number(
    payload?.totalPages ??
      metadata.totalPages ??
      payload?.total_pages ??
      metadata.total_pages ??
      fallback.totalPages ??
      calculatedTotalPages
  );

  return {
    number: Number.isFinite(number) ? number : 0,
    size: Number.isFinite(size) && size > 0 ? size : items.length,
    totalPages: Math.max(Number.isFinite(totalPages) ? totalPages : calculatedTotalPages, 1),
    totalElements
  };
}
