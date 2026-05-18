export function money(value) {
  if (value === null || value === undefined || Number.isNaN(Number(value))) return "Price on request";
  return new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 0
  }).format(value);
}

export function nights(query) {
  const start = new Date(query.checkInDate);
  const end = new Date(query.checkOutDate);
  const diff = Math.ceil((end - start) / 86400000);
  return Number.isFinite(diff) && diff > 0 ? diff : 1;
}

export function firstPhoto(item, fallback) {
  return item?.photos?.[0] || fallback;
}

export function displayHotelName(hotel) {
  return hotel?.hotelName || hotel?.name || `Hotel #${hotel?.id || "new"}`;
}

export function csvToList(value) {
  return String(value || "")
    .split(",")
    .map((item) => item.trim())
    .filter(Boolean);
}

export function listToCsv(value) {
  return Array.isArray(value) ? value.join(", ") : "";
}
